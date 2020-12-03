import networkx as nx

from utils import read_write_utils, conversion_utils, sort_utils
from similarity import wikipedia2vecsim


def calculate_score(contextrank, wiki2vecobj, entity1_embed, entity2_embed, query_sim):
    return ((1/contextrank)*wiki2vecobj.calculate_cosine_sim(entity1_embed, entity2_embed))+query_sim


def create_relations_graph(input_json, wiki2vecobj, converted_ids):

    query_graphobj_mapping = dict()
    counter = 1

    for item in input_json:
        if item['queryid'] not in query_graphobj_mapping:
            G = nx.Graph()
            query_graphobj_mapping[item['queryid']] = G
        for relation in item['relAnnotations']:
            sub_ann = []
            obj_ann = []

            for s_ann in relation['subjectAnnotations']:
                sub_ann.extend(s_ann['wiki_converted_id'])
            for o_ann in relation['objectAnnotations']:
                obj_ann.extend(o_ann['wiki_converted_id'])

            for s in sub_ann:
                for o in obj_ann:
                    try:
                        s_title = converted_ids[s]
                        s_embedding = wiki2vecobj.get_entity_embedding(s_title)
                        o_title = converted_ids[o]
                        o_embedding = wiki2vecobj.get_entity_embedding(o_title)
                        if query_graphobj_mapping[item['queryid']].has_edge(s, o):
                            query_graphobj_mapping[item['queryid']][s][o]['weight'] = query_graphobj_mapping[item['queryid']][s][o]['weight'] + calculate_score(int(item['contextrank']),
                                                                                                                                                                wiki2vecobj,
                                                                                                                                                                s_embedding,
                                                                                                                                                                o_embedding,
                                                                                                                                                                relation['query_sim_glove'])
                        else:
                            query_graphobj_mapping[item['queryid']].add_edge(s, o, weight=calculate_score(int(item['contextrank']),
                                                                                                          wiki2vecobj,
                                                                                                          s_embedding,
                                                                                                          o_embedding,
                                                                                                          relation['query_sim_glove']))
                    except KeyError as ke:
                        print('keyerror : {} {} {}'.format(ke, s_title, o_title))
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

def rerank_entities(scored_entities, wiki2vecobj, converted_ids):
    for query, ent in scored_entities.items():
        query_title = query.replace("enwiki:","").replace("%20"," ")
        try:
            query_embedding = wiki2vecobj.get_entity_embedding(query_title)
            for entity, score in ent.items():
                entity_title = converted_ids[entity]
                entity_embedding = wiki2vecobj.get_entity_embedding(entity_title)
                scored_entities[query][entity] = score * wiki2vecobj.calculate_cosine_sim(query_embedding, entity_embedding)
        except KeyError as ke:
            print('keyerror : {} {} {}'.format(ke, query_title, entity_title))
    return scored_entities


def relation_relevance_query_relation_similarity_wrapper(input, embedding_txt_file, conversion_folder_loc, output):
    print("inside relation relevance entity similarity wrapper")
    inputjson = read_write_utils.read_multiple_json_files(input)
    entity_converted_ids = read_write_utils.read_converted_entity_ids_reversed(conversion_folder_loc)
    wiki2vecobj = wikipedia2vecsim.Wikipedia2VecSimilarity(embedding_txt_file)
    query_map_json = create_relations_graph(inputjson, wiki2vecobj, entity_converted_ids)
    querygraphjson = convert_map_to_output(query_map_json)
    queryjson = rerank_entities(querygraphjson, wiki2vecobj, entity_converted_ids)
    sorted_queryjson = sort_utils.sort_elements_by_value(queryjson)
    output_list = conversion_utils.convert_entity_counter_dict_to_trec_format(sorted_queryjson, 'relations_relevance_entity_wikipedia2vec_similarity')
    read_write_utils.write_text_file(output, output_list)