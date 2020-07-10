import os
import argparse
import json

def set_default(obj):
    if isinstance(obj, set):
        return list(obj)
    raise TypeError

def process_json_files(input_json_dir, output):
    files = os.listdir(input_json_dir)
    query_list = dict()
    item_list = []
    print(len(files))
    for file in files:
        with open(input_json_dir+file,'r',encoding='utf-8') as f:
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
    print(len(query_list))
    item_list.append(query_list)
    with open(output, 'w', encoding='utf-8') as f:
            json.dump(item_list, f, default=set_default)

if __name__ == "__main__":
    parser = argparse.ArgumentParser("Please provide input JSON directory, Qrel file and output file path")
    parser.add_argument("--i",help="Input JSON folder location")
    parser.add_argument("--q",help="Input qrel file location")
    parser.add_argument("--o",help="Output JSON file location")
    args = parser.parse_args()
    process_json_files(args.i, args.o)