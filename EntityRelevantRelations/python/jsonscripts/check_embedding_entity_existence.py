import os
import argparse
from wikipedia2vec import Wikipedia2Vec


def load_embedding(embedding_txt_file):
    wiki2vec = Wikipedia2Vec.load(embedding_txt_file)
    return wiki2vec

def read_converted_entity_ids(folder_location):
    files = os.listdir(folder_location)
    converted_entiy_ids = set()
    try:
        for file in files:
            with open(folder_location+'/'+file, 'r', encoding='utf-8') as f:
                for line in f:
                    data = line.split('\t')

                    # the titles contain \n at the end, hack to resolve that
                    title = data[1].split('\n')[0]
                    converted_entiy_ids.add(title)
        return converted_entiy_ids
    except Exception as e:
        print(e)
        return None

def get_missing_embedding_entities_number(converted_entities, wiki2vecobj):

    total_converted_entities = 0 # total titles in converted entities folder
    embed_entities_present = 0 # total titles present in embedding
    embed_entities_absent = 0 # total titles missing in embedding

    for title in converted_entities:
        total_converted_entities += 1
        try:
            query_embedding = wiki2vecobj.get_entity_vector(title)
            embed_entities_present += 1
        except KeyError:
            embed_entities_absent += 1
        print(total_converted_entities)
    print("Total titles in converted entities folder : {}".format(total_converted_entities))
    print("Total converted entities titles present in embedding : {}".format(embed_entities_present))
    print("Total converted entities titles absent in embedding : {}".format(embed_entities_absent))



if __name__ == "__main__":
    parser = argparse.ArgumentParser("Please provide input entity conversion directory, embedding model file location")
    parser.add_argument("--i", help="Input entity conversion folder location")
    parser.add_argument("--e", help="Input embedding model file location")
    args = parser.parse_args()
    converted_entities_set = read_converted_entity_ids(args.i)
    embed_obj = load_embedding(args.e)
    get_missing_embedding_entities_number(converted_entities_set, embed_obj)