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


def find_common_entities(json_dict, qrel_dict):
    common_entities_list = []
    for query,ent in qrel_dict.items():
        if query in json_dict:
            common_entities_dict = dict()
            common_entities_dict['queryid'] = query
            common_entities_dict['total_qrel_entities'] = len(ent)
            common_entities_dict['common_entities'] = len(ent & json_dict[query])
            common_entities_dict['difference_entities'] = len(ent - json_dict[query])
            common_entities_list.append(common_entities_dict)
    return common_entities_list


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


def process_json_files(input_json_dir):
    files = os.listdir(input_json_dir)
    query_list = dict()
    #item_list = []
    print(len(files))
    for file in files:
        with open(input_json_dir+'/'+file, 'r', encoding='utf-8') as f:
            print(os.path.abspath(file))
            json_decode = json.load(f)
            print(len(json_decode))
            for query in json_decode:
                query_id = query.get("queryid")
                val = set()
                if query_id in query_list:
                    val = query_list[query_id]
                for ent in query.get('WATEntitiesTitle'):
                    val.add(ent)
                    query_list[query_id] = val
    #print(query_list)
    return query_list
    #item_list.append(query_list)
    #with open(output, 'w', encoding='utf-8') as f:
    #        json.dump(item_list, f, default=set_default)


if __name__ == "__main__":
    parser = argparse.ArgumentParser("Please provide input JSON directory, Qrel file and output file path")
    parser.add_argument("--i", help="Input JSON folder location")
    parser.add_argument("--q", help="Input qrel file location")
    parser.add_argument("--o", help="Output JSON file location")
    args = parser.parse_args()
    json_dict = process_json_files(args.i)
    qrel_dict = process_qrel_files(args.q)
    common_entities = find_common_entities(json_dict, qrel_dict)
    write_csv_file(args.o, common_entities)
