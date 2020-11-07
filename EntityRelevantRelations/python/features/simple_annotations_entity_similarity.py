from utils import read_write_utils, conversion_utils, sort_utils
from similarity import wikipedia2vecsim


def calculate_score(contextrank, wiki2vecobj, entity1_embed, entity2_embed):
    return (1/contextrank)*wiki2vecobj.calculate_cosine_sim(entity1_embed, entity2_embed)

def get_entity_ranking(inputjson, wiki2vecobj, conversion_ids):

    output_dict = dict()

    for item in inputjson:
        query_title = item['queryid'].replace("enwiki:","").replace("%20"," ")
        try:
            query_embedding = wiki2vecobj.get_entity_embedding(query_title)
            for ent in item['WATannotations']:
                if ent['wiki_title'] in conversion_ids:

                        ent_embedding = wiki2vecobj.get_entity_embedding(ent['wiki_title'])
                        converted_id = conversion_ids[ent['wiki_title']][0]
                        if item['queryid'] in output_dict:
                            if converted_id in output_dict[item['queryid']]:
                                output_dict[item['queryid']][converted_id] = output_dict[item['queryid']][converted_id] + calculate_score(int(item['contextrank']), wiki2vecobj, query_embedding, ent_embedding)
                            else:
                                output_dict[item['queryid']][converted_id] = calculate_score(int(item['contextrank']), wiki2vecobj, query_embedding, ent_embedding)
                        else:
                            inner_dict = dict()
                            inner_dict[converted_id] = calculate_score(int(item['contextrank']), wiki2vecobj, query_embedding, ent_embedding)
                            output_dict[item['queryid']] = inner_dict
        except KeyError as ke:
            print('keyerror : {} {} {}'.format(ke, query_title, ent['wiki_title']))

    return output_dict


def entity_similarity_wrapper(input, embedding_txt_file, conversion_folder_loc, output):
    print("inside entity similarity wrapper")
    inputjson = read_write_utils.read_multiple_json_files(input)
    entity_converted_ids = read_write_utils.read_converted_entity_ids(conversion_folder_loc)
    wiki2vecobj = wikipedia2vecsim.Wikipedia2VecSimilarity(embedding_txt_file)
    queryjson = get_entity_ranking(inputjson, wiki2vecobj, entity_converted_ids)
    sorted_queryjson = sort_utils.sort_elements_by_value(queryjson)
    output_list = conversion_utils.convert_entity_counter_dict_to_trec_format(sorted_queryjson, 'annotations_entity_wikipedia2vec_similarity')
    read_write_utils.write_text_file(output, output_list)