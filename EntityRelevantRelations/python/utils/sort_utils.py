import heapq

from operator import itemgetter


def sort_elements(inputjson, limit):

    sortedinputjson = dict()
    for key, val in inputjson.items():
        sorted_dict = heapq.nlargest(limit, val.items(), key=itemgetter(1))
        sortedinputjson[key] = dict(sorted_dict)
    return sortedinputjson
