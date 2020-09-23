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
                para_relational_neighbors = dict()
                for rel in item['relAnnotations']:
                    sub_ann = []
                    obj_ann = []
                    for sub in rel['subjectAnnotations']:
                        sub_ann.extend(sub['wiki_converted_id'])
                    for obj in rel['objectAnnotations']:
                        obj_ann.extend(obj['wiki_converted_id'])

                    for s in sub_ann:
                        for o in obj_ann:
                            if s in para_relational_neighbors:
                                if o not in para_relational_neighbors[s]:
                                    para_relational_neighbors[s].append(o)
                            else:
                                para_relational_neighbors[s] = [o]

                    for obj in obj_ann:
                        for sub in sub_ann:
                            if obj in para_relational_neighbors:
                                if sub not in para_relational_neighbors[obj]:
                                    para_relational_neighbors[obj].append(sub)
                            else:
                                para_relational_neighbors[obj] = [sub]

                for neghbr in para_relational_neighbors:
                    ann_dict = dict()
                    ann_doc = dict()
                    ann_dict['query'] = item['queryid']
                    ann_dict['rank'] = 1
                    ann_dict['score'] = 1
                    ann_dict['method'] = method_name
                    ann_doc['paragraph'] = item['contextid']
                    ann_doc['neighbor'] = para_relational_neighbors[neghbr]
                    ann_doc['entity'] = neghbr
                    ann_dict['document'] = ann_doc
                    output_list.append(ann_dict)
                print(counter)
                counter = counter + 1
    return output_list


if __name__ == "__main__":
    parser = argparse.ArgumentParser("Please provide relation annotations folder, output file location and method name")
    parser.add_argument('--a', help='relation annotations folder location')
    parser.add_argument('--o', help='output jsonl associations file location')
    parser.add_argument('--m', help='method name')
    args = parser.parse_args()
    output_data = generate_assocations_file(args.a, args.m)
    write_jsonl_file(args.o, output_data)
