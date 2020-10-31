#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Sat Oct 24 17:42:14 2020

@author: poojaoza
"""
import json
import os
import argparse
import networkx as nx
import matplotlib.pyplot as plt
import seaborn as sns

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


def create_relations_graph(input_json, query, qrel):

    G = nx.Graph()

    for item in input_json:
        if item['queryid'] == query:
            for relation in item['relAnnotations']:
                sub_ann = []
                obj_ann = []

                for s_ann in relation['subjectAnnotations']:
                    sub_ann.extend(s_ann['wiki_converted_id'])
                for o_ann in relation['objectAnnotations']:
                    obj_ann.extend(o_ann['wiki_converted_id'])

                for s in sub_ann:
                    for o in obj_ann:
                        if s in qrel_data[query] and o in qrel_data[query]:
                            if not G.has_edge(s, o):
                                G.add_edge(s, o, weight=1)
                            else:
                                G[s][o]['weight'] = G[s][o]['weight'] + 1
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

def create_heatmap(G, query):
    fig, ax = plt.subplots(figsize=(30, 30))
    title = query
    plt.title(title, fontsize=10)
    A = nx.to_pandas_adjacency(G)
    sns.heatmap(A, ax=ax, annot=True, fmt="d")
    #plt.yticks(rotation=0)
    plt.savefig('query1.png')

if __name__ == "__main__":
    parser = argparse.ArgumentParser("Please provide relation annotation folder location,\
                                     query and output folder location")
    parser.add_argument("-a", "--annotations", help="Input relation annotations JSON folder location")
    parser.add_argument("-qrel", "--qrel", help="Qrel file location")
    parser.add_argument("-q", "--query", help="Query to generate heatmap for")
    parser.add_argument("-o", "--output", help="output png image file location")
    args = parser.parse_args()
    input_data = read_multiple_json_files(args.annotations)
    print("read annotations files")
    qrel_data = process_qrel_files(args.qrel)
    print("read qrel file")
    query_graph = create_relations_graph(input_data, args.query, qrel_data)
    print("generated graph")
    create_heatmap(query_graph, args.query)
    print("generated heatmap file")