import argparse

from utils import read_write_utils


def map_relation_triples_wat_ann(rel_dict, ann_dict, ent_dict):

    query_rel_ann_list = []
    counter = 1
    for query_con in rel_dict:
        query_con_dict = dict()
        rel_ann_list = []
        for triple in rel_dict[query_con]['relationTriples']:
            rel_ann_dict = dict()
            subject_tokens_list = []
            for token in triple['subjectTokens']:
                for ann in ann_dict[query_con]['WATannotations']:
                    if int(token['charOffsetBegin']) >= int(ann['start']) and int(token['charOffsetEnd']) <= int(ann['end']):

                        # if the title already exists in the list then no need to add it again.
                        # title_exists checks whether it exists in the subjectAnnotations/subjectTokens
                        title_exists = False
                        for t in subject_tokens_list:
                            if t['wiki_title'] == ann['wiki_title']:
                                title_exists = True
                                break
                        if not title_exists:
                            subject_token_dict = dict()
                            subject_token_dict['wiki_id'] = ann['wiki_id']
                            subject_token_dict['wiki_title'] = ann['wiki_title']
                            if ann['wiki_title'] in ent_dict:
                                subject_token_dict['wiki_converted_id'] = ent_dict[ann['wiki_title']]
                            else:
                                subject_token_dict['wiki_converted_id'] = []
                            subject_tokens_list.append(subject_token_dict)
            object_tokens_list = []
            for token in triple['objectTokens']:
                for ann in ann_dict[query_con]['WATannotations']:
                    if int(token['charOffsetBegin']) >= int(ann['start']) and int(token['charOffsetEnd']) <= int(ann['end']):

                        # if the title already exists in the list then no need to add it again.
                        # title_exists checks whether it exists in the objectAnnotations/objectTokens
                        title_exists = False
                        for t in object_tokens_list:
                            if t['wiki_title'] == ann['wiki_title']:
                                title_exists = True
                                break
                        if not title_exists:
                            object_token_dict = dict()
                            object_token_dict['wiki_id'] = ann['wiki_id']
                            object_token_dict['wiki_title'] = ann['wiki_title']
                            if ann['wiki_title'] in ent_dict:
                                object_token_dict['wiki_converted_id'] = ent_dict[ann['wiki_title']]
                            else:
                                object_token_dict['wiki_converted_id'] = []
                            object_tokens_list.append(object_token_dict)
            rel_ann_dict['subject'] = triple['subject']
            rel_ann_dict['relation'] = triple['relation']
            rel_ann_dict['object'] = triple['object']
            rel_ann_dict['subjectAnnotations'] = subject_tokens_list
            rel_ann_dict['objectAnnotations'] = object_tokens_list
            rel_ann_list.append(rel_ann_dict)
        query_con_id = query_con.split('_')
        query_con_dict['queryid'] = query_con_id[0]
        query_con_dict['contextid'] = query_con_id[1]
        query_con_dict['contexttext'] = rel_dict[query_con]['contexttext']
        query_con_dict['contextrank'] = rel_dict[query_con]['contextrank']
        query_con_dict['contextscore'] = rel_dict[query_con]['contextscore']
        query_con_dict['relAnnotations'] = rel_ann_list
        query_rel_ann_list.append(query_con_dict)
        print(counter)
        counter = counter+1

    return query_rel_ann_list


def create_relation_triples_wat_ann_dicts(rel_file, ann_file, ent_file):

    relation_triples = read_write_utils.read_json_file(rel_file)
    annotations = read_write_utils.read_json_file(ann_file)
    entity_ids = read_write_utils.read_converted_entity_ids(ent_file)

    rel_triples_dict = dict()
    ann_dict = dict()

    for item in relation_triples:
        rel_triples_details = dict()
        rel_triples_details['contexttext'] = item['contexttext']
        rel_triples_details['contextrank'] = item['contextrank']
        rel_triples_details['contextscore'] = item['contextscore']
        rel_triples_details['relationTriples'] = item['relationTriples']
        rel_triples_dict[item['queryid']+'_'+item['contextid']] = rel_triples_details

    for item in annotations:
        ann_details = dict()
        ann_details['contexttext'] = item['contexttext']
        ann_details['contextrank'] = item['contextrank']
        ann_details['contextscore'] = item['contextscore']
        ann_details['WATannotations'] = item['WATannotations']
        ann_dict[item['queryid']+'_'+item['contextid']] = ann_details

    return rel_triples_dict, ann_dict, entity_ids


def relation_triples_wat_annotations(rel_triples, annotations, ent_conv, output_file):
    rel_tri, wat_ann, ent_conv_id = create_relation_triples_wat_ann_dicts(rel_triples, annotations, ent_conv)
    output_json = map_relation_triples_wat_ann(rel_tri, wat_ann, ent_conv_id)
    read_write_utils.write_json_file(output_file, output_json)


if __name__ == "__main__":
    parser = argparse.ArgumentParser("Please provid input relation triple file, WAT Annotations file, "
                                     "entity conversion folder, output file locations")
    parser.add_argument('--r', help='relation triple file location')
    parser.add_argument('--w', help='WAT Annptations file location')
    parser.add_argument('--e', help='Entity conversions folder location')
    parser.add_argument('--o', help='json output file location')
    args = parser.parse_args()
    relation_triples_wat_annotations(args.r, args.w, args.e, args.o)
