import os
import numpy as np

from utils import read_write_utils, conversion_utils, sort_utils
from similarity import glove

def get_average_vector(terms_length, ids_dict, embed_vec):

    # normalize each word vector to unit variance
    Q = np.zeros((terms_length, 300))

    for terms, ind in ids_dict.items():
        Q[ind, :] = embed_vec[terms]

    # Q_norm = np.zeros(Q.shape)
    # d = (np.sum(Q ** 2, 1) ** (0.5))
    # Q_norm = (Q.T / d).T
    Q_avg = np.average(Q, axis=0)

    return Q_avg

def get_query_relation_similarity(input_json, glove_obj):

    final_output = []
    counter = 1
    for item in input_json:
        query_title = item['queryid'].replace("enwiki:","").replace("%20"," ")
        query_embed = dict()
        try:
            query_words = query_title.split()
            present_query_words = []
            for w in query_words:
                w_lower = w.lower()
                if glove_obj.check_term_existence(w_lower):
                    query_embed[w_lower] = glove_obj.get_word_embedding(w_lower)
                    present_query_words.append(w_lower)

            query_ids = {w.lower():ind for ind, w in enumerate(present_query_words)}
            query_size = len(present_query_words)

            Q_avg = get_average_vector(query_size, query_ids, query_embed)

            for relation in item['relAnnotations']:
                relation_embed = dict()
                relation_triple = relation['subject']+' '+relation['relation']+' '+relation['object']
                relation_words = relation_triple.split()
                present_relation_words = []

                for r in relation_words:
                    r_lower = r.lower()
                    if glove_obj.check_term_existence(r_lower):
                        relation_embed[r_lower] = glove_obj.get_word_embedding(r_lower)
                        present_relation_words.append(r_lower)

                relation_ids = {r.lower():ind for ind, r in enumerate(present_relation_words)}
                relation_size = len(present_relation_words)

                R_avg = get_average_vector(relation_size, relation_ids, relation_embed)

                relation['query_sim_glove'] = glove_obj.get_cosine_similarity(Q_avg, R_avg)
                final_output.append(item)
        except Exception as e:
            print('{} {} {}'.format(e, item['queryid'], item['contextid']))

        print(counter)
        counter += 1

    return final_output

def retrieve_input_json(input_file, glove_obj, output_file):
    # files = os.listdir(input_folder)
    # for file in files:
    content_json = read_write_utils.read_json_file(input_file)
    modified_json = get_query_relation_similarity(content_json, glove_obj)
    #read_write_utils.write_json_file(output_folder+'/'+file, modified_json)
    read_write_utils.write_json_file(output_file, modified_json)

def query_relation_similarity_wrapper(input, embedding_txt_file, output):
    print("inside query relation similarity wrapper")
    glove_obj = glove.GloveSimilarity(embedding_txt_file)
    retrieve_input_json(input, glove_obj, output)