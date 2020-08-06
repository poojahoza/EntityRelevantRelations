import argparse

from collections import OrderedDict

from utils import readwriteutils


def count_entities(input_json):
    query_json = dict()
    for item in input_json:
        for relation in item:
            if item['queryid'] in query_json:
                for entityid in relation['wiki_converted_id']:
                    if entityid in query_json[item['queryid']]:
                        query_json[item['queryid']][entityid] = query_json[item['queryid']][entityid] + 1
                    else:
                        query_json[item['queryid']][entityid] = 1
            else:
                entities_dict = OrderedDict()
                for entityid in relation['wiki_converted_id']:
                    entities_dict[entityid] = 1
                query_json[item['queryid']] = entities_dict
    return query_json


if __name__ == "__main__":
    parser = argparse.ArgumentParser("Please enter relation triples annotations file")
    parser.add_argument('--r', help='relation triple file location')
    args = parser.parse_args()
    inputjson = readwriteutils.read_json_file(args.i)
    queryjson = count_entities(inputjson)
    print(len(queryjson))
