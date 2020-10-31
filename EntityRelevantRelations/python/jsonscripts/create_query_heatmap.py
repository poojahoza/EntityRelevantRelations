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
    

def create_relations_graph(input_json, query):

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
                        if not G.has_edge(s, o):
                            G.add_edge(s, o)
    return G


def create_heatmap(G, query):
    fig, ax = plt.subplots(figsize=(30, 30))
    title = query
    plt.title(title, fontsize=10)
    A = nx.to_numpy_matrix(G)
    heatmap = sns.heatmap(A, ax=ax, xticklabels=True)
    heatmap.set_xticklabels(heatmap.get_xmajorticklabels(), fontsize = 0.5, rotation=90)
    plt.savefig('query1.png')
    
if __name__ == "__main__":
    parser = argparse.ArgumentParser("Please provide relation annotation folder location,\
                                     query and output folder location")
    parser.add_argument("-a", "--annotations", help="Input relation annotations JSON folder location")
    parser.add_argument("-q", "--query", help="Query to generate heatmap for")
    parser.add_argument("-o", "--output", help="output png image file location")
    args = parser.parse_args()
    input_data = read_multiple_json_files(args.annotations)
    print("read annotations files")
    query_graph = create_relations_graph(input_data, args.query)
    print("generated graph")
    create_heatmap(query_graph, args.query)
    print("generated heatmap file")