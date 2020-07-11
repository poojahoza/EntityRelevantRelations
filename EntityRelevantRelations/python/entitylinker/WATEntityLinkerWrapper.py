import json
import argparse
import WATEntityLinker

def writeToFile(output_file, final_dict):
    with open(output_file, 'w', encoding='utf-8') as f:
        json.dump(output_file, final_dict)

def fetchFilesFromFolder(input_file):
    final_json = []
    with open(input_file,'r',encoding='utf-8') as f:
        json_decode = json.load(f)
        for item in json_decode:
            temp_item = dict()
            wat_annotations = WATEntityLinker.wat_entity_linking(item['contexttext'])
            temp_item['queryid'] = item['queryid']
            temp_item['contexttext'] = item['contexttext']
            temp_item['contextid'] = item['contextid']
            temp_item['contextrank'] = item['contextrank']
            temp_item['contextscore'] = item['contextscore']
            temp_item['WATannotations'] = wat_annotations
            final_json.append(temp_item)

if __name__ == "__main__":
    parser = argparse.ArgumentParser("Please provide input file and output file location")
    parser.add_argument("--i",help="Input JSON file location")
    parser.add_argument("--o",help="Output JSON file location")
    args = parser.parse_args()
    final_json = fetchFilesFromFolder(args.i)
    writeToFile(args.o, final_json)