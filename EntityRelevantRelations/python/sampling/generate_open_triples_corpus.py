import codecs
import gensim

from utils import read_write_utils


def unicode_text(text):
    terms = []
    for t in text.split():
        t = gensim.utils.to_unicode(t.lower())
        if t == u'':
            continue
        terms.append(t)

    return u' '.join(terms)

def get_flat_relation_triples(input_json):

    relation_corpus = ''
    for item in input_json:
        for relation in item['relAnnotations']:
            relation_corpus = relation_corpus+' '+relation['subject']+' '+relation['relation']+' '+relation['object']
    return [relation_corpus]

    # with codecs.open(output_file, 'w', encoding='utf-8') as f:
    #     for item in input_json:
    #         for relation in item['relAnnotations']:
    #             relation_triple = u'{0} {1} {2}'.format(relation['subject'], relation['relation'], relation['object'])
    #             triple = unicode_text(relation_triple)
    #             f.write(triple + u'\n')

def generate_open_triples_corpus_wrapper(input_folder_loc, output_file_location):
    input_data = read_write_utils.read_multiple_json_files(input_folder_loc)
    print("read annotations file")
    output_data = get_flat_relation_triples(input_data)
    print("generated output string")
    read_write_utils.write_text_file(output_file_location, output_data)
    print("generated corpus file")
