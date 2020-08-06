import json
import argparse

from collections import OrderedDict


def read_json_file(input_file):
    with open(input_file, 'r', encoding='utf-8') as f:
        json_dict = json.load(f)
    return json_dict


def count_entities(input_json, field):

    if field == "subject":
        ann_field = "subjectAnnotations"
    else:
        ann_field = "objectAnnotations"

    query_json = dict()
    for item in input_json:
        for relation in item['relAnnotations']:
            if item['queryid'] in query_json:
                for ann in relation[ann_field]:
                    for entityid in ann['wiki_converted_id']:
                        if entityid in query_json[item['queryid']]:
                            query_json[item['queryid']][entityid] = query_json[item['queryid']][entityid] + 1
                        else:
                            query_json[item['queryid']][entityid] = 1
            else:
                entities_dict = OrderedDict()
                for ann in relation[ann_field]:
                    for entityid in ann['wiki_converted_id']:
                        entities_dict[entityid] = 1
                query_json[item['queryid']] = entities_dict
    return query_json


if __name__ == "__main__":
    parser = argparse.ArgumentParser("Please enter relation triples annotations file")
    parser.add_argument('-a', '--annotationsfile', help='relation triple file location')
    parser.add_argument('-f', '--field', help='field subject | object')
    args = parser.parse_args()
    if not (args.field == "subject" or args.field == "object"):
        print("the value of field flag must be subject | object")
        sys.exit(-1)
    inputjson = read_json_file(args.annotationsfile)
    queryjson = count_entities(inputjson, args.field)
    print(len(queryjson))
