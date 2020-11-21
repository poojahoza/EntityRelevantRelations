import os
import argparse
import json
import csv

from collections import OrderedDict
from stanfordnlp.server import CoreNLPClient
from stanfordnlp.server.client import AnnotationException

def read_multiple_json_files(folder_location):
    files = os.listdir(folder_location)
    content_json = []
    try:
        for file in files:
            with open(folder_location+'/'+file, 'r', encoding='utf-8') as f:
                content_json.extend(json.load(f))
        return content_json
    except Exception as e:
        print(e)
        return None

def process_qrel_files(input_qrel_file):
    qrel_list = dict()
    with open(input_qrel_file, 'r', encoding='utf-8') as f:
        for line in f:
            data = line.split(" ")
            query_id = data[0]
            ent = data[2]
            val = set()
            if query_id in qrel_list:
                val = qrel_list[query_id]
            val.add(ent)
            qrel_list[query_id] = val
    #print(qrel_list)
    return qrel_list

def write_output_tsv_file(output_list, output_loc):
    with open(output_loc, 'w') as f:
        writer = csv.writer(f, delimiter='\t', newline='\n')
        for record in output_list:
            writer.writerow([record['query'], record['p1'], record['p2'], str(record['label'])])

def get_entity_context(entity_spot, ordered_relation_ann, start_position_list):

    subject_relation_start_pos = int(entity_spot['start'])
    if subject_relation_start_pos in ordered_relation_ann:
        for pos, val in enumerate(start_position_list):
            if val == subject_relation_start_pos:
                p1_ent_men_start_index = pos
        p1_left_token_start_index = p1_ent_men_start_index-5 if p1_ent_men_start_index-5>=0 else 0
        p1_window_charoffsetbegin_index = start_position_list[p1_left_token_start_index]

        # get the end index of the entity mention, 'spot' of entity mention can span for several
        # token, the if condition checks whether the stanford token index ends before or after
        # the spot end index. p1_ent_men_ent_index is the end index of the entity mention.
        # We need 5 tokens after the end index of this entity mention
        p1_ent_men_end_index = ordered_relation_ann[subject_relation_start_pos][0] \
            if ordered_relation_ann[subject_relation_start_pos][0] > int(entity_spot['end']) \
            else int(entity_spot['end'])

        # The end index of the entity mention i.e. p1_ent_men_end_index is the start index
        # of the next token. We check whether the p1_ent_men_end_index appears as the start
        # index for the next token, or we get the start index of the next token.
        for ind, val in enumerate(start_position_list):
            if val >= p1_ent_men_end_index:
                temp_index = ind

        p1_right_token_start_index = temp_index+5 \
            if temp_index+5 < len(start_position_list) else len(start_position_list)-1

        p1_window_charoffsetend_index = ordered_relation_ann[start_position_list[p1_right_token_start_index]][0]

        return p1_window_charoffsetbegin_index, p1_window_charoffsetend_index

def generate_sampling(input_data, qrel_data):

    '''
    Generates sampling for Siamese similarity metric. The output is in the format
    query paragraph1 paragraph2 label
    label 0 - positive
    label 1 - negative
    If subject and object entities are relevant entities, then it is positive sample
    If either subject or object is relevant and the other is non-relevant, then it is negative sample
    We do not consider those relations where both subject and object entities are non-relevant.

    paragraph1 and paragraph2: is the context around the entity mention. We take window of 5. 5 tokens before entity
    mention and 5 tokens after the mention

    :param input_data: the relation annotations
    :param qrel_data: the qrel (ground truth) data
    :return: List of dictionary [{'query':q1, 'p1':p1, 'p2':p2, 'label':0}]
    '''

    output_list = []
    counter = 1
    with CoreNLPClient(properties={'annotators': 'tokenize,ssplit'}, timeout=30000, memory='16G') as client:
        for item in input_data:
            try:
                annotated_text = client.annotate(item['contexttext'])
                relation_ann = dict()
                for sentence in annotated_text.sentence:
                    for token in sentence.token:
                        relation_ann[int(token.beginChar)] = [int(token.endChar),token.word]

                ordered_relation_ann = OrderedDict(sorted(relation_ann.items(), key=lambda t:t[0]))
                start_position_list = list(ordered_relation_ann.keys())
                sub_entities = []
                obj_entities = []

                for relation in item['relAnnotations']:
                    for sub in relation['subjectAnnotations']:
                        sub_entities.extend(sub['wiki_converted_id'])
                    for obj in relation['objectAnnotations']:
                        obj_entities.extend(obj['wiki_converted_id'])

                    common_sub_entities = set(qrel_data[item['queryid']]) & set(sub_entities)
                    common_obj_entities = set(qrel_data[item['queryid']]) & set(obj_entities)

                    if (len(common_sub_entities) > 0 and len(common_obj_entities) == 0) or \
                        (len(common_sub_entities) == 0 and len(common_obj_entities) > 0):

                        if len(common_sub_entities) > 0:
                            for sub_rel in relation['subjectAnnotations']:

                                # This if condition is to handle the case of more than one entity in subjectAnnotations.
                                # For example, in case of 2 subject entities if 1 is relevant and other is non-relevant
                                # and all object entities are non-relevant, then we want only those labels where 1-relevant
                                # subject entity is paired with non-relevant object entities. The other non-relevant subject
                                # entity is discarded.
                                if len(set(sub_rel['wiki_converted_id']) & common_sub_entities) > 0:
                                    p1_context_start_index, p1_context_end_index = get_entity_context(sub_rel,
                                                                                                ordered_relation_ann,
                                                                                                start_position_list)
                                    p1 = item['contexttext'][p1_context_start_index:p1_context_end_index]

                                    for obj_rel in relation['objectAnnotations']:
                                        p2_context_start_index, p2_context_end_index = get_entity_context(obj_rel,
                                                                                                          ordered_relation_ann,
                                                                                                          start_position_list)
                                        p2 = item['contexttext'][p2_context_start_index:p2_context_end_index]
                                        output_list.append({'query':item['queryid'],'p1':p1,'p2':p2,'label':1})

                        if len(common_obj_entities) > 0:
                            for sub_rel in relation['subjectAnnotations']:
                                p1_context_start_index, p1_context_end_index = get_entity_context(sub_rel,
                                                                                                  ordered_relation_ann,
                                                                                                  start_position_list)
                                p1 = item['contexttext'][p1_context_start_index:p1_context_end_index]

                                for obj_rel in relation['objectAnnotations']:
                                    if len(set(obj_rel['wiki_converted_id']) & common_obj_entities) > 0:
                                        p2_context_start_index, p2_context_end_index = get_entity_context(obj_rel,
                                                                                                          ordered_relation_ann,
                                                                                                          start_position_list)
                                        p2 = item['contexttext'][p2_context_start_index:p2_context_end_index]
                                        output_list.append({'query':item['queryid'],'p1':p1,'p2':p2,'label':1})

                    elif len(common_sub_entities) >0 and len(common_obj_entities) >0:
                        for sub_rel in relation['subjectAnnotations']:
                            if len(set(sub_rel['wiki_converted_id']) & common_sub_entities) > 0:
                                p1_context_start_index, p1_context_end_index = get_entity_context(sub_rel,
                                                                                                  ordered_relation_ann,
                                                                                                  start_position_list)
                                p1 = item['contexttext'][p1_context_start_index:p1_context_end_index]

                            for obj_rel in relation['objectAnnotations']:
                                if len(set(obj_rel['wiki_converted_id']) & common_obj_entities) > 0:
                                    p2_context_start_index, p2_context_end_index = get_entity_context(obj_rel,
                                                                                                      ordered_relation_ann,
                                                                                                      start_position_list)
                                    p2 = item['contexttext'][p2_context_start_index:p2_context_end_index]
                                    output_list.append({'query':item['queryid'],'p1':p1,'p2':p2,'label':0})
            except AnnotationException:
                print(item['queryid']+" "+item['contextid']+" AnnotationException")
            print(counter)
            counter += 1

    return output_list


if __name__ == "__main__":
    parser = argparse.ArgumentParser("Please provide relation annotation folder location \
                                     and output folder location")
    parser.add_argument("-a", "--annotations", help="Input relation annotations JSON folder location")
    parser.add_argument("-qrel", "--qrel", help="Qrel file location")
    parser.add_argument("-o", "--output", help="output png image file location")
    args = parser.parse_args()
    input_data = read_multiple_json_files(args.annotations)
    print("read annotations files")
    qrel_data = process_qrel_files(args.qrel)
    print("read qrel file")
    output_data = generate_sampling(input_data, qrel_data)
    print("generated output samples")
    write_output_tsv_file(output_data, args.output)
    print("generated tsv file")

