from utils import read_write_utils, conversion_utils, sort_utils


def count_entities(input_json, field):

    if field == "subject":
        ann_field = "subjectAnnotations"
    elif field == "object":
        ann_field = "objectAnnotations"
    else:
        ann_field = "both"

    query_json = dict()
    for item in input_json:
        if ann_field == "both":
            for relation in item['relAnnotations']:
                if item['queryid'] in query_json:
                    entity_set = set()
                    for sann in relation["subjectAnnotations"]:
                        for entityid in sann['wiki_converted_id']:
                            entity_set.add(entityid)
                    for oann in relation["objectAnnotations"]:
                        for entityid in oann['wiki_converted_id']:
                            entity_set.add(entityid)
                    for entid in entity_set:
                        if entid in query_json[item['queryid']]:
                            query_json[item['queryid']][entid] = query_json[item['queryid']][entid] + 1
                        else:
                            query_json[item['queryid']][entid] = 1
                else:
                    entities_dict = dict()
                    entity_set = set()
                    for sann in relation["subjectAnnotations"]:
                        for entityid in sann['wiki_converted_id']:
                            entity_set.add(entityid)
                    for oann in relation["objectAnnotations"]:
                        for entityid in oann['wiki_converted_id']:
                            entity_set.add(entityid)
                    for entid in entity_set:
                        entities_dict[entid] = 1
                    query_json[item['queryid']] = entities_dict
        else:
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
    sorted_queryjson = sort_utils.sort_elements(queryjson, lim)
    output_list = conversion_utils.convert_entity_counter_dict_to_trec_format(sorted_queryjson, 'rel_ann_entity_freq')
    read_write_utils.write_text_file(output, output_list)
