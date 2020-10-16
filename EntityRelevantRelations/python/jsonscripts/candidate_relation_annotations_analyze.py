import json
import argparse
import os
import pandas as pd


def write_csv_file(output_file, output_dict):
    l = []
    for item in output_dict:
        l.append(pd.DataFrame(item, index=[0]))
    tmp = pd.concat(l)
    tmp.index.name = 'Queries'
    #tmp = tmp.rename(columns={0:'Total',1:'Common',2:'Difference'})
    tmp.to_csv(output_file)


def convert_dict_to_list(output_json, sub_dict, obj_dict, both_dict, both_non_rel_dict, qrel_dict):
    output_list = []

    for key, val in output_json.items():
        query_json = dict()
        query_json['queryid'] = key
        query_json['total_relations'] = val['total_relations']
        query_json['subject_relations_present'] = val['subject_relations_present']
        query_json['object_relations_present'] = val['object_relations_present']
        query_json['both_relations_present'] = val['both_relations_present']
        query_json['relevant_subject_relations_present'] = val['relevant_subject_relations_present']
        query_json['relevant_object_relations_present'] = val['relevant_object_relations_present']
        query_json['relevant_both_relations_present'] = val['relevant_both_relations_present']
        query_json['both_non_relevant_relations_present'] = val['both_non_relevant_relations_present']
        query_json['all_relevant_relations_list'] = val['all_relevant_relations_list']
        query_json['both_relevant_relations_list'] = val['both_relevant_relations_list']
        query_json['total_qrel_entities'] = len(qrel_dict[key])
        query_json['subject_relevant_qrel_common_entities'] = len(sub_dict[key] & qrel_dict[key])
        query_json['object_relevant_qrel_common_entities'] = len(obj_dict[key] & qrel_dict[key])
        query_json['both_relevant_qrel_common_entities'] = len(both_dict[key] & qrel_dict[key])
        query_json['relevant_qrel_common_entities'] = len((sub_dict[key].union(obj_dict[key]).union(both_dict[key])) & qrel_dict[key])
        output_list.append(query_json)

    return output_list


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

def process_input_json_files(input_json_dir_loc, qrel_dict):
    output_json = dict()
    sub_relevant_query_dict = dict()
    obj_relevant_query_dict = dict()
    both_relevant_query_dict = dict()
    both_non_relevant_query_dict = dict()
    files = os.listdir(input_json_dir_loc)
    print(len(files))
    for file in files:
        with open(input_json_dir_loc+'/'+file, 'r', encoding='utf-8') as f:
            print(os.path.abspath(file))
            json_decode = json.load(f)
            print(len(json_decode))
            for item in json_decode:
                query_id = item["queryid"]
                total_relations = len(item['relAnnotations'])
                subject_relations = 0  # how many relations have subject entities mapped
                object_relations = 0   # how many relations have object entities mapped
                both_relations = 0     # how many relations have both subject and object entities mapped
                rel_sub_relations = 0  # how many relevant entities are present in subject if entities are present in both subject and object
                rel_obj_relations = 0  # how many relevant entities are present in object if entities are present in both subject and object
                rel_both_relations = 0 # how many relevant entities are present in both subject and object
                non_rel_both_relations = 0 # how many relevant entities are present in both subject and object
                all_relevant_relations = ''
                both_relevant_relations = ''

                for relation in item['relAnnotations']:
                    sub_ann = []
                    obj_ann = []

                    for s_ann in relation['subjectAnnotations']:
                        sub_ann.extend(s_ann['wiki_converted_id'])
                        if len(s_ann['wiki_converted_id']) > 0:
                            subject_relations = subject_relations + 1
                    for o_ann in relation['objectAnnotations']:
                        obj_ann.extend(o_ann['wiki_converted_id'])
                        if len(o_ann['wiki_converted_id']) > 0:
                            object_relations = object_relations + 1

                    if len(sub_ann) > 0 and len(obj_ann) > 0:
                        both_relations = both_relations + 1
                        qrel_rel_set = set(qrel_dict[query_id])
                        sub_rel_set = set(sub_ann)
                        obj_rel_set = set(obj_ann)
                        sub_intersection_set_len = len(qrel_rel_set & sub_rel_set)
                        obj_intersection_set_len = len(qrel_rel_set & obj_rel_set)
                        all_relevant_relations = all_relevant_relations+'\n'+relation['relation']
                        if sub_intersection_set_len > 0:
                            rel_sub_relations = rel_sub_relations + 1
                            if query_id in sub_relevant_query_dict:
                                rel_set = sub_relevant_query_dict[query_id]
                            else:
                                rel_set = set()
                            for r in sub_ann:
                                rel_set.add(r)
                            sub_relevant_query_dict[query_id] = rel_set
                        if obj_intersection_set_len > 0:
                            rel_obj_relations = rel_obj_relations + 1
                            if query_id in obj_relevant_query_dict:
                                rel_set = obj_relevant_query_dict[query_id]
                            else:
                                rel_set = set()
                            for r in obj_ann:
                                rel_set.add(r)
                            obj_relevant_query_dict[query_id] = rel_set
                        if sub_intersection_set_len >0 and obj_intersection_set_len > 0:
                            rel_both_relations = rel_both_relations + 1
                            both_relevant_relations = both_relevant_relations+'\n'+relation['relation']
                            if query_id in both_relevant_query_dict:
                                rel_set = both_relevant_query_dict[query_id]
                            else:
                                rel_set = set()
                            for r in obj_ann:
                                rel_set.add(r)
                            for r1 in sub_ann:
                                rel_set.add(r1)
                            both_relevant_query_dict[query_id] = rel_set

                        if sub_intersection_set_len == 0 and obj_intersection_set_len == 0:
                            non_rel_both_relations = non_rel_both_relations + 1
                            if query_id in both_non_relevant_query_dict:
                                non_rel_set = both_non_relevant_query_dict[query_id]
                            else:
                                non_rel_set = set()
                            for r in obj_ann:
                                non_rel_set.add(r)
                            for r1 in sub_ann:
                                non_rel_set.add(r1)
                            both_non_relevant_query_dict[query_id] = non_rel_set

                if query_id not in sub_relevant_query_dict:
                    sub_relevant_query_dict[query_id] = set()
                if query_id not in obj_relevant_query_dict:
                    obj_relevant_query_dict[query_id] = set()
                if query_id not in both_relevant_query_dict:
                    both_relevant_query_dict[query_id] = set()
                if query_id not in both_non_relevant_query_dict:
                    both_non_relevant_query_dict[query_id] = set()

                if query_id in output_json:
                    output_json[query_id]['total_relations'] = output_json[query_id]['total_relations'] + total_relations
                    output_json[query_id]['subject_relations_present'] = output_json[query_id]['subject_relations_present'] + subject_relations
                    output_json[query_id]['object_relations_present'] = output_json[query_id]['object_relations_present'] + object_relations
                    output_json[query_id]['both_relations_present'] = output_json[query_id]['both_relations_present'] + both_relations
                    output_json[query_id]['relevant_subject_relations_present'] = output_json[query_id]['relevant_subject_relations_present'] + rel_sub_relations
                    output_json[query_id]['relevant_object_relations_present'] = output_json[query_id]['relevant_object_relations_present'] + rel_obj_relations
                    output_json[query_id]['relevant_both_relations_present'] = output_json[query_id]['relevant_both_relations_present'] + rel_both_relations
                    output_json[query_id]['both_non_relevant_relations_present'] = output_json[query_id]['both_non_relevant_relations_present'] + non_rel_both_relations
                    output_json[query_id]['all_relevant_relations_list'] = output_json[query_id]['all_relevant_relations_list'] + all_relevant_relations
                    output_json[query_id]['both_relevant_relations_list'] = output_json[query_id]['both_relevant_relations_list'] + both_relevant_relations
                else:
                    item_json = dict()
                    item_json['total_relations'] = total_relations
                    item_json['subject_relations_present'] = subject_relations
                    item_json['object_relations_present'] = object_relations
                    item_json['both_relations_present'] = both_relations
                    item_json['relevant_subject_relations_present'] = rel_sub_relations
                    item_json['relevant_object_relations_present'] = rel_obj_relations
                    item_json['relevant_both_relations_present'] = rel_both_relations
                    item_json['both_non_relevant_relations_present'] = non_rel_both_relations
                    item_json['all_relevant_relations_list'] = all_relevant_relations
                    item_json['both_relevant_relations_list'] = both_relevant_relations
                    output_json[query_id] = item_json

    return (output_json, sub_relevant_query_dict, obj_relevant_query_dict, both_relevant_query_dict, both_non_relevant_query_dict)


if __name__ == "__main__":
    parser = argparse.ArgumentParser("Please provide input JSON directory and output file path")
    parser.add_argument("--i", help="Input JSON folder location")
    parser.add_argument("--q", help="Qrel file location")
    parser.add_argument("--o", help="Output csv file location")
    args = parser.parse_args()
    qrel_dict = process_qrel_files(args.q)
    json_dict, sub_dict, obj_dict, both_dict, both_non_rel_dict = process_input_json_files(args.i, qrel_dict)
    #print(sub_dict)
    output_list = convert_dict_to_list(json_dict, sub_dict, obj_dict, both_dict, both_non_rel_dict, qrel_dict)
    write_csv_file(args.o, output_list)
