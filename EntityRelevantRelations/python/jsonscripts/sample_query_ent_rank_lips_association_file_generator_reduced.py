import argparse
import json
import jsonlines
import os

from collections import defaultdict


def write_jsonl_file(output_file, output_list):
    with jsonlines.open(output_file, mode='w') as f:
        for a in output_list:
            f.write(a)

def process_qrel_file(qrel_file_path):
    print("processing qrel file")
    qrel_dict = defaultdict(list)
    with open(qrel_file_path, 'r') as qrel_file:
        for line in qrel_file:
            line_split = line.strip('\n').split()
            if int(line_split[3]) == 1:
                qrel_dict[line_split[0]].append(line_split[2])
    # print(qrel_dict)
    return qrel_dict

def generate_assocations_file(input_folder, method_name, qrel_dict, query, paraid):

    output_list = []
    counter = 0

    files = os.listdir(input_folder)
    for file in files:
        with open(input_folder+'/'+file, 'r') as f:
            input_json = json.load(f)
            for item in input_json:
                if item['queryid'] == query and item['contextid'] == paraid:
                    for rel in item['relAnnotations']:
                        sub_ann = []
                        obj_ann = []
                        for sub in rel['subjectAnnotations']:
                            sub_ann.extend(sub['wiki_converted_id'])
                        for obj in rel['objectAnnotations']:
                            obj_ann.extend(obj['wiki_converted_id'])

                        for s in sub_ann:
                            for o in obj_ann:
                                if s in qrel_dict[item['queryid']] and o not in qrel_dict[item['queryid']]:
                                    ann_dict = dict()
                                    ann_doc = dict()
                                    ann_dict['query'] = item['queryid']
                                    ann_dict['rank'] = 1
                                    ann_dict['score'] = 1
                                    ann_dict['method'] = method_name
                                    ann_doc['paragraph'] = item['contextid']
                                    ann_doc['neighbor'] = []
                                    ann_doc['entity'] = [s, o]
                                    ann_dict['document'] = ann_doc
                                    output_list.append(ann_dict)
                print(counter)
                counter = counter + 1
    return output_list


if __name__ == "__main__":
    parser = argparse.ArgumentParser("Please provide relation annotations folder, qrel file loc, output file location, query, paragraph id and method name")
    parser.add_argument('--a', help='relation annotations folder location')
    parser.add_argument('--q', help='entity qrel file location')
    parser.add_argument('--o', help='output jsonl associations file location')
    parser.add_argument('--pid', help='paragraph id')
    parser.add_argument('--query', help='sample query')
    parser.add_argument('--m', help='method name')
    args = parser.parse_args()
    qrel_data = process_qrel_file(args.q)
    output_data = generate_assocations_file(args.a, args.m, qrel_data, args.query, args.pid)
    write_jsonl_file(args.o, output_data)
