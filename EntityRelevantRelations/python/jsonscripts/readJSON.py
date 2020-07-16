import json
import argparse
import sys
import os

from collections import Counter

def process_json_file(input, output, entitycounter, query):
    input_file = open(input, 'r', encoding='utf-8')
    #output_file = open(output, 'w', encoding='utf-8')

    json_decode = json.load(input_file)
    item_list = []
    wat_items = Counter()
    for item in json_decode:

        if item.get('queryid')==query:
            print("in if %s",item.get('queryid'))
            items = dict()
            items['queryid'] = item.get('queryid')
            items['contextid'] = item.get('contextid')
            items['contexttext'] = item.get('contexttext')
            items['contextrank'] = item.get('contextrank')
            items['WATEntitiesTitle'] = item.get('WATEntitiesTitle')
            for ent in item.get('WATEntitiesTitle'):
                wat_items[ent] += 1
            item_list.append(items)
    with open(output, 'w', encoding='utf-8') as f:
        json.dump(item_list, f)
    with open(entitycounter, 'w', encoding='utf-8') as f1:
            json.dump(wat_items, f1)
    #main_json = json.dump(item_list, output_file)
    print(wat_items)
    #output_file.write(main_json)
    #output_file.close()

if __name__ == "__main__":
    parser = argparse.ArgumentParser("Please provide input file and output file")
    parser.add_argument("--i",help="Input JSON file location")
    parser.add_argument("--o",help="Output JSON file location")
    parser.add_argument("--entitycounter",help="Output Entity Counter file location")
    parser.add_argument("--q",help="query id to be processed")
    args = parser.parse_args()
    process_json_file(args.i, args.o, args.entitycounter, args.q)