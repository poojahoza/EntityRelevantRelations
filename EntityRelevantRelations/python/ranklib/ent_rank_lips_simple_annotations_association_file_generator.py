import argparse
import json
import jsonlines
import os

from collections import defaultdict


def write_jsonl_file(output_file, output_list):
    with jsonlines.open(output_file, mode='w') as f:
        for a in output_list:
            f.write(a)

def generate_assocations_file(input_folder, method_name):

    output_list = []
    counter = 0

    files = os.listdir(input_folder)
    for file in files:
        with open(input_folder+'/'+file, 'r') as f:
            input_json = json.load(f)
            for item in input_json:
                item_entites_set = set(item['WATEntitiesTitle'])
                for rel in item_entites_set:
                    ann_dict = dict()
                    ann_doc = dict()
                    ann_dict['query'] = item['queryid']
                    ann_dict['rank'] = 1
                    ann_dict['score'] = 1
                    ann_dict['method'] = method_name
                    ann_doc['paragraph'] = item['contextid']
                    ann_doc['neighbor'] = []
                    ann_doc['entity'] = rel
                    ann_dict['document'] = ann_doc
                    output_list.append(ann_dict)
                print(counter)
                counter = counter + 1
    return output_list


if __name__ == "__main__":
    parser = argparse.ArgumentParser("Please provide WAT annotations (entity links) folder, output file location and method name")
    parser.add_argument('--a', help='WAT annotations folder location')
    parser.add_argument('--o', help='output jsonl associations file location')
    parser.add_argument('--m', help='method name')
    args = parser.parse_args()
    output_data = generate_assocations_file(args.a, args.m)
    write_jsonl_file(args.o, output_data)
