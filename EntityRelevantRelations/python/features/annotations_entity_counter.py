import json
import argparse
import heapq

from operator import itemgetter

from utils import read_write_utils


def sort_elements(self, inputjson, limit):

    for key, val in inputjson.items():
        sorted_dict = heapq.nlargest(limit, val.items(), key=itemgetter(1))
        inputjson[key] = dict(sorted_dict)
    return inputjson


def count_entities(self, input_json, field):

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
                entities_dict = dict()
                for ann in relation[ann_field]:
                    for entityid in ann['wiki_converted_id']:
                        entities_dict[entityid] = 1
                query_json[item['queryid']] = entities_dict
    return query_json


def annotations_entity_counter_wrapper(input, field, lim, output):
    inputjson = read_write_utils.read_multiple_json_files(input)
    queryjson = count_entities(inputjson, field)
    sorted_queryjson = sort_elements(queryjson, lim)
    read_write_utils.write_multiple_json_files(output, sorted_queryjson, 'rel-ann-entity-freq')
