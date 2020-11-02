import math
import argparse
import os
import json
import powerlaw
import networkx as nx
import numpy as np
import matplotlib.pyplot as plt


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


def create_relations_graph(input_json, qrel_data, field):

    G = nx.MultiGraph()

    for item in input_json:
        for relation in item['relAnnotations']:
            sub_ann = []
            obj_ann = []

            for s_ann in relation['subjectAnnotations']:
                sub_ann.extend(s_ann['wiki_converted_id'])
            for o_ann in relation['objectAnnotations']:
                obj_ann.extend(o_ann['wiki_converted_id'])

            for s in sub_ann:
                for o in obj_ann:
                    if field == "positive":
                        if s in qrel_data[item['queryid']] and o in qrel_data[item['queryid']]:
                            G.add_edge(s, o)
                    elif field == "negative":
                        if s not in qrel_data[item['queryid']] and o not in qrel_data[item['queryid']]:
                            G.add_edge(s, o)
    return G

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

def create_power_law_degree_distribution(query_graph, output_folder_loc):

    degree_seq = sorted([d for n,d in query_graph.degree()], reverse=True)
    fit = powerlaw.Fit(degree_seq)
    fig2 = plt.figure(figsize=(30, 30))
    try:
        fig2 = fit.plot_pdf(color='b', linewidth=2)
        fit.power_law.plot_pdf(color='g', linestyle='--', ax=fig2)
        plt.savefig(output_folder_loc+key+'.png')
    except ValueError as ve:
        print(ve)

if __name__ == "__main__":
    parser = argparse.ArgumentParser("Please provide relation annotation folder location \
                                     and output folder location")
    parser.add_argument("-a", "--annotations", help="Input relation annotations JSON folder location")
    parser.add_argument("-qrel", "--qrel", help="Qrel file location")
    parser.add_argument("-f", "--field", help="choice of only postive relations or negative relations", choices=["positive", "negative"])
    parser.add_argument("-o", "--output", help="output png image file location")
    args = parser.parse_args()
    input_data = read_multiple_json_files(args.annotations)
    print("read annotations files")
    qrel_data = process_qrel_files(args.qrel)
    print("read qrel file")
    query_graph = create_relations_graph(input_data, qrel_data, args.field)
    print("generated graphs")
    create_power_law_degree_distribution(query_graph, args.output)
    print("generated power law files")
