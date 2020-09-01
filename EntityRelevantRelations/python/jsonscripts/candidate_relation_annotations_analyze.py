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


def convert_dict_to_list(output_json):
    output_list = []

    for key, val in output_json.items():
        query_json = dict()
        query_json['queryid'] = key
        query_json['total_relations'] = val['total_relations']
        query_json['subject_relations_present'] = val['subject_relations_present']
        query_json['object_relations_present'] = val['object_relations_present']
        query_json['both_relations_present'] = val['both_relations_present']
        output_list.append(query_json)

    return output_list


def process_input_json_files(input_json_dir_loc):
    output_json = dict()
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

                if query_id in output_json:
                    output_json[query_id]['total_relations'] = output_json[query_id]['total_relations'] + total_relations
                    output_json[query_id]['subject_relations_present'] = output_json[query_id]['subject_relations_present'] + subject_relations
                    output_json[query_id]['object_relations_present'] = output_json[query_id]['object_relations_present'] + object_relations
                    output_json[query_id]['both_relations_present'] = output_json[query_id]['both_relations_present'] + both_relations
                else:
                    item_json = dict()
                    item_json['total_relations'] = total_relations
                    item_json['subject_relations_present'] = subject_relations
                    item_json['object_relations_present'] = object_relations
                    item_json['both_relations_present'] = both_relations
                    output_json[query_id] = item_json

    return output_json


if __name__ == "__main__":
    parser = argparse.ArgumentParser("Please provide input JSON directory and output file path")
    parser.add_argument("--i", help="Input JSON folder location")
    parser.add_argument("--o", help="Output csv file location")
    args = parser.parse_args()
    json_dict = process_input_json_files(args.i)
    output_list = convert_dict_to_list(json_dict)
    write_csv_file(args.o, output_list)
