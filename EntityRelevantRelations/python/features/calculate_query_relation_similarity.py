import os
import numpy as np

from utils import read_write_utils, conversion_utils, sort_utils
from similarity import glove

def get_average_vector(terms_length, ids_dict, embed_vec):

    # normalize each word vector to unit variance
    Q = np.zeros((terms_length, 300))

    for terms, ind in ids_dict.items():
        Q[ind, :] = embed_vec[terms]

    Q_norm = np.zeros(Q.shape)
    d = (np.sum(Q ** 2, 1) ** (0.5))
    Q_norm = (Q.T / d).T
    Q_avg = np.average(Q_norm, axis=0)

    return Q_avg

def get_query_relation_similarity(input_json, glove_obj):

    final_output = []
    counter = 1
    for item in input_json:
        query_title = item['queryid'].replace("enwiki:","").replace("%20"," ")
        query_embed = dict()
        try:
            query_words = query_title.split()
            for w in query_words:
                query_embed[w.lower()] = glove_obj.get_word_embedding(w.lower())

            query_ids = {w.lower():ind for ind, w in enumerate(query_words)}
            query_size = len(query_words)

            Q_avg = get_average_vector(query_size, query_ids, query_embed)

            for relation in item['relAnnotations']:
                relation_embed = dict()
                relation_triple = relation['subject']+' '+relation['relation']+' '+relation['object']
                relation_words = relation_triple.split()

                for r in relation_words:
                    relation_embed[r.lower()] = glove_obj.get_word_embedding(r.lower())

                relation_ids = {r.lower():ind for ind, r in enumerate(relation_words)}
                relation_size = len(relation_words)

                R_avg = get_average_vector(relation_size, relation_ids, relation_embed)

                relation['query_sim_glove'] = glove_obj.get_cosine_similarity(Q_avg, R_avg)
                final_output.append(item)
        except Exception as e:
            print('{} {} {}'.format(e, item['queryid'], item['contextid']))

        print(counter)
        counter += 1

    return final_output

def retrieve_input_json(input_folder, glove_obj, output_folder):
    files = os.listdir(input_folder)
    for file in files:
        content_json = read_write_utils.read_json_file(input_folder+'/'+file)
        modified_json = get_query_relation_similarity(content_json, glove_obj)
        read_write_utils.write_json_file(output_folder+'/'+file, modified_json)

def query_relation_similarity_wrapper(input, embedding_txt_file, output):
    print("inside query relation similarity wrapper")
    glove_obj = glove.GloveSimilarity(embedding_txt_file)
    retrieve_input_json(input, glove_obj, output)