from sklearn.metrics.pairwise import cosine_similarity

class GloveSimilarity:

    def __init__(self,embed_loc_file):
        with open(embed_loc_file, 'r') as f:
            self.vectors = {}
        for line in f:
            vals = line.rstrip().split(' ')
            self.vectors[vals[0]] = [float(x) for x in vals[1:]]

    def get_word_embedding(self, word):
        return self.vectors[word]

    def get_cosine_similarity(self, word1_embed, word2_embed):
        return cosine_similarity(word1_embed.reshape(1, -1), word2_embed.reshape(1, -1))[0].tolist()[0]