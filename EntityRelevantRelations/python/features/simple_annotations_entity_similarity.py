import os

from wikipedia2vec import Wikipedia2Vec
from sklearn.metrics.pairwise import cosine_similarity

from utils import read_write_utils, conversion_utils, sort_utils


def load_embedding(embedding_binary_file):
    wiki2vec = Wikipedia2Vec.load(embedding_binary_file)
    return wiki2vec

def get_entity_converted_ids(conversion_folder_loc):
    files = os.listdir(conversion_folder_loc)
    converted_ids = dict()

    try:
        for file in files:
            with open(conversion_folder_loc+'/'+file, 'r', encoding='utf-8') as f:
                for line in f.readlines():
                    splitted_text = line.split('\t')
                    if splitted_text[1] in converted_ids:
                        converted_ids[splitted_text[1]] = converted_ids[splitted_text[1]].append(splitted_text[0])
                    else:
                        converted_ids[splitted_text[1]] = [splitted_text[0]]
        return converted_ids
    except Exception as e:
        print(e)
        return None



def calculate_entity_similarity(inputjson, wiki2vecobj, conversion_ids):

    output_dict = dict()

    for item in inputjson:
        query_title = item['queryid'].replace("enwiki:","").replace("%20"," ")
        query_embedding = wiki2vecobj.get_entity_vector(query_title)
        for ent in item['WATannotations']:
            if ent['wiki_title'] in conversion_ids:
                try:
                    ent_embedding = wiki2vecobj.get_entity_vector(ent['wiki_title'])
                    converted_id = conversion_ids[ent['wiki_title']][0]
                    if converted_id in output_dict[item['queryid']]:
                        output_dict[item['queryid']][converted_id] = output_dict[item['queryid']][converted_id] + ((1/int(item['contextrank']))*cosine_similarity(query_embedding.reshape(1, -1), ent_embedding.reshape(1, -1)))
                    else:
                        output_dict[item['queryid']][converted_id] = ((1/int(item['contextrank']))*cosine_similarity(query_embedding.reshape(1, -1), ent_embedding.reshape(1, -1)))
                except KeyError:
                    print('keyerror for entity : '+ent['wiki_title'])

    return output_dict


def entity_similarity_wrapper(input, embedding_bin_file, conversion_folder_loc, output):
    print("inside entity similarity wrapper")
    inputjson = read_write_utils.read_multiple_json_files(input)
    entity_converted_ids = get_entity_converted_ids(conversion_folder_loc)
    wiki2vecobj = load_embedding(embedding_bin_file)
    queryjson = calculate_entity_similarity(inputjson, wiki2vecobj, entity_converted_ids)
    sorted_queryjson = sort_utils.sort_elements_by_value(queryjson)
    output_list = conversion_utils.convert_entity_counter_dict_to_trec_format(sorted_queryjson, 'annotations_entity_wikipedia2vec_similarity')
    read_write_utils.write_text_file(output, output_list)