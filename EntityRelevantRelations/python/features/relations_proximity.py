import networkx as nx

from utils import read_write_utils, conversion_utils, sort_utils


def create_relations_graph(input_json):

    query_graphobj_mapping = dict()
    counter = 1

    for item in input_json:
        if item['queryid'] not in query_graphobj_mapping:
            G = nx.Graph()
            query_graphobj_mapping[item['queryid']] = G
        for relation in item['relAnnotations']:

            for subj in relation['subjectAnnotations']:
                for obj in relation['objectAnnotations']:
                    for s in subj['wiki_converted_id']:
                        for o in obj['wiki_converted_id']:
                            if query_graphobj_mapping[item['queryid']].has_edge(s, o):
                                query_graphobj_mapping[item['queryid']][s][o]['weight'] = query_graphobj_mapping[item['queryid']][s][o]['weight'] + abs(s['end']-o['start'])
                            else:
                                query_graphobj_mapping[item['queryid']].add_edge(s, o, weight=(abs(s['end']-o['start'])))

        print(counter)
        counter = counter + 1
    return query_graphobj_mapping


def convert_map_to_output(query_graph_map):

    output_dict = dict()

    for key, graph in query_graph_map.items():
        if key not in output_dict:
            G = query_graph_map[key]
            nodes = list(G.nodes)
            nodes_dict = dict()
            for n in nodes:
                edge_weight = 0
                node_edges = G.edges(n, data=True)
                for edge in node_edges:
                    edge_weight = edge_weight + edge[2]['weight']
                nodes_dict[n] = edge_weight
            output_dict[key] = nodes_dict
    return output_dict


def relation_proximity_wrapper(input, output):
    inputjson = read_write_utils.read_multiple_json_files(input)
    query_map_json = create_relations_graph(inputjson)
    queryjson = convert_map_to_output(query_map_json)
    sorted_queryjson = sort_utils.sort_elements_by_value(queryjson)
    output_list = conversion_utils.convert_entity_counter_dict_to_trec_format(sorted_queryjson, 'relation_edge_weight_proximity')
    read_write_utils.write_text_file(output, output_list)
