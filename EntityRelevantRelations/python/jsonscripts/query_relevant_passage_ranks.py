import os
import argparse
import json
import pandas as pd


def set_default(obj):
    if isinstance(obj, set):
        return list(obj)
    raise TypeError


def write_json_file(output_file, output_dict):
    with open(output_file, 'w', encoding='utf-8') as f:
        json.dump(output_dict, f)


def write_csv_file(output_file, output_dict):
    l = []
    for item in output_dict:
        l.append(pd.DataFrame(item, index=[0]))
    tmp = pd.concat(l)
    tmp.index.name = 'Queries'
    #tmp = tmp.rename(columns={0:'Total',1:'Common',2:'Difference'})
    tmp.to_csv(output_file)


def find_relevant_passage_rank(json_dict, qrel_dict, query):
    relevant_entities = dict()
    query_dict = dict()
    for ent in qrel_dict[query]:
        relevant_entities[ent] = []
    for item in json_dict:
        if item['queryid'] == query:
            for relation in item['relAnnotations']:
                entity_set = set()
                for sann in relation["subjectAnnotations"]:
                    for entityid in sann['wiki_converted_id']:
                        entity_set.add(entityid)
                for oann in relation["objectAnnotations"]:
                    for entityid in oann['wiki_converted_id']:
                        entity_set.add(entityid)
            for entity in entity_set:
                if entity in relevant_entities:
                    ent_list = relevant_entities[entity]
                    ent_list.append(item['contextrank'])
                    relevant_entities[entity] = ent_list
    query_dict[query] = relevant_entities
    return query_dict


def process_qrel_files(input_qrel_file):
    qrel_list = dict()
    with open(input_qrel_file,'r', encoding='utf-8') as f:
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


if __name__ == "__main__":
    parser = argparse.ArgumentParser("Please provide input JSON directory, Qrel file and output file path")
    parser.add_argument("--i", help="Input JSON folder location")
    parser.add_argument("--q", help="Input qrel file location")
    parser.add_argument("--Q", help="Query to search for")
    parser.add_argument("--o", help="Output json file location")
    args = parser.parse_args()
    json_dict = read_multiple_json_files(args.i)
    qrel_dict = process_qrel_files(args.q)
    common_entities = find_relevant_passage_rank(json_dict, qrel_dict, args.Q)
    write_json_file(args.o, common_entities)
