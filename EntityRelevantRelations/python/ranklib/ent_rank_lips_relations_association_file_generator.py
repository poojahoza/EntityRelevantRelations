import argparse
import json
import jsonlines
import os

import networkx as nx


def write_jsonl_file(output_file, output_list):
    with jsonlines.open(output_file, mode='w') as f:
        for a in output_list:
            f.write(a)

def generate_assocations_file(input_folder, method_name):

    output_list = []
    query_graphobj_mapping = dict()
    counter = 0

    files = os.listdir(input_folder)
    for file in files:
        with open(input_folder+'/'+file, 'r') as f:
            input_json = json.load(f)
            for item in input_json:
                if item['queryid'] not in query_graphobj_mapping:
                    query_para_dict = dict()
                    query_para_dict[item['contextid']] = nx.Graph()
                    query_graphobj_mapping[item['queryid']] = query_para_dict
                else:
                    if item['contextid'] not in query_graphobj_mapping[item['queryid']]:
                        query_graphobj_mapping[item['queryid']][item['contextid']] = nx.Graph()
                for relation in item['relAnnotations']:
                    sub_ann = []
                    obj_ann = []

                    for s_ann in relation['subjectAnnotations']:
                        sub_ann.extend(s_ann['wiki_converted_id'])
                    for o_ann in relation['objectAnnotations']:
                        obj_ann.extend(o_ann['wiki_converted_id'])

                    for s in sub_ann:
                        for o in obj_ann:
                            if not query_graphobj_mapping[item['queryid']][item['contextid']].has_edge(s, o):
                                query_graphobj_mapping[item['queryid']][item['contextid']].add_edge(s, o)

    for query, val in query_graphobj_mapping.items():
        for para, graph in val.items():
            nodes = list(graph.nodes)
            for n in nodes:
                ann_dict = dict()
                ann_doc = dict()
                ann_dict['query'] = query
                ann_dict['rank'] = 1
                ann_dict['score'] = 1
                ann_dict['method'] = method_name
                ann_doc['paragraph'] = para
                ann_doc['neighbor'] = [nr for nr in graph.neighbors(n)]
                ann_doc['entity'] = n
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
