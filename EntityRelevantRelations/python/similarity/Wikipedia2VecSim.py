from wikipedia2vec import Wikipedia2Vec
from sklearn.metrics.pairwise import cosine_similarity

class Wikipedia2VecSim:


    def __init__(self, embed_file_loc):
        self.wiki2vec = Wikipedia2Vec.load(embed_file_loc)


    def get_word_embedding(self, word):
        return self.wiki2vec.get_word_vector(word)

    def get_entity_embedding(self, entity):
        return self.wiki2vec.get_entity_vector(entity)

    def calculate_cosine_sim(self, entity1_embed, entity2_embed):
        return cosine_similarity(entity1_embed.reshape(1, -1), entity2_embed.reshape(1, -1))[0].tolist()[0]