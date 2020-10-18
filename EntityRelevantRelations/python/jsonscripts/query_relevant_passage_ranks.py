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
    # l = []
    # for item in output_dict:
    #     l.append(pd.DataFrame(item, index=[1]))
    # tmp = pd.concat(l)
    tmp = pd.DataFrame(output_dict)
    #tmp.index.name = 'Queries'
    #tmp = tmp.rename(columns={0:'Total',1:'Common',2:'Difference'})
    tmp.to_csv(output_file)


def convert_dict_to_pandas_format(output_data):

    final_list = []

    for query, entities in output_data.items():
        for ent, rank in entities.items():
            ent_dict = {'query':query,
                        'relent':ent,
                        'contextrank': ','.join(map(str, list(sorted(rank['contextrank'])))),
                        'total_paras':len(rank['contextrank']),
                        'total_relations':rank['relations']}
            final_list.append(ent_dict)

    return final_list

def get_relevant_passage_data(item, intersection_set, output_data):

    for element in intersection_set:
        if item['queryid'] in output_data:
            if element in output_data[item['queryid']]:
                rank_set = output_data[item['queryid']][element]['contextrank']
                rank_set.add(int(item['contextrank']))
                relations_no = output_data[item['queryid']][element]['relations']
                relations_no = relations_no + 1
                output_data[item['queryid']][element]['contextrank'] = rank_set
                output_data[item['queryid']][element]['relations'] = relations_no
            else:
                output_data[item['queryid']][element] = {'contextrank':set([int(item['contextrank'])]), 'relations':1}
        else:
            rel_entities = dict()
            rel_entities[element] = {'contextrank':set([int(item['contextrank'])]), 'relations':1}
            output_data[item['queryid']] = rel_entities

    return output_data

def find_relevant_passage_rank(json_dict, qrel_dict):
    output_data = dict()

    for item in json_dict:
        for relation in item['relAnnotations']:
            sub_ann = []
            obj_ann = []

            for s_ann in relation['subjectAnnotations']:
                sub_ann.extend(s_ann['wiki_converted_id'])
            for o_ann in relation['objectAnnotations']:
                obj_ann.extend(o_ann['wiki_converted_id'])

            if len(sub_ann) > 0 and len(obj_ann) > 0:
                qrel_rel_set = set(qrel_dict[item['queryid']])
                sub_set = set(sub_ann)
                obj_set = set(obj_ann)

                sub_intersection_set = qrel_rel_set & sub_set
                obj_intersection_set = qrel_rel_set & obj_set


                if len(sub_intersection_set) > 0:
                    output_data = get_relevant_passage_data(item, sub_intersection_set, output_data)

                if len(obj_intersection_set) > 0:
                    output_data = get_relevant_passage_data(item, obj_intersection_set, output_data)

    return output_data

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
    #parser.add_argument("--Q", help="Query to search for")
    parser.add_argument("--o", help="Output json file location")
    args = parser.parse_args()
    json_dict = read_multiple_json_files(args.i)
    qrel_dict = process_qrel_files(args.q)
    #common_entities = find_relevant_passage_rank(json_dict, qrel_dict, args.Q)
    common_entities = find_relevant_passage_rank(json_dict, qrel_dict)
    final_entities_list = convert_dict_to_pandas_format(common_entities)
    #write_json_file(args.o, common_entities)
    write_csv_file(args.o, final_entities_list)
