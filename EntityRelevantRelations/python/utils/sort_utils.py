import heapq

from operator import itemgetter


def sort_elements(inputjson, limit):

    sortedinputjson = dict()
    for key, val in inputjson.items():
        sorted_dict = heapq.nlargest(limit, val.items(), key=itemgetter(1))
        sortedinputjson[key] = dict(sorted_dict)
    return sortedinputjson


def sort_elements_by_value(inputjson):

    sortedjson = dict()
    for key, value in inputjson.items():
        sortedjson[key] = {k:v for k, v in sorted(value.items(), key=lambda item: item[1], reverse=True)}
    return sortedjson
