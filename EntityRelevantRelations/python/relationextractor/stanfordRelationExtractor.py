import json
import argparse
import os
import sys

from stanfordnlp.server import CoreNLPClient
from stanfordnlp.server.client import AnnotationException

from utils import readwriteutils


def check_file_existence(output_file_name):
    if os.path.exists(output_file_name):
        os.remove(output_file_name)


def build_triples_charoffset_list(tokens_dict, sentence_json, annotated_text):
    relation_tokens=[]
    for subtoken in tokens_dict:
        try:
            token_details = dict()
            token_details['token'] = sentence_json[str(subtoken.sentenceIndex)][str(subtoken.tokenIndex)]['token']
            token_details['charOffsetBegin'] = sentence_json[str(subtoken.sentenceIndex)][str(subtoken.tokenIndex)]['charOffsetBegin']
            token_details['charOffsetEnd'] = sentence_json[str(subtoken.sentenceIndex)][str(subtoken.tokenIndex)]['charOffsetEnd']
            relation_tokens.append(token_details)
        except KeyError:
            for s1 in annotated_text.sentence:
                if s1.sentenceIndex == subtoken.sentenceIndex:
                    for t in s1.token:
                        if t.tokenBeginIndex == subtoken.tokenIndex:
                            token_details = dict()
                            token_details['token'] = t.word
                            token_details['charOffsetBegin'] = t.beginChar
                            token_details['charOffsetEnd'] = t.endChar
                            relation_tokens.append(token_details)
                            break
                    break
    return relation_tokens


def extract_relations(input1, coref_flag):
    output_json = []
    input_json = readwriteutils.read_json_file(input1)
    error_log = []
    with CoreNLPClient(properties={'annotators': 'tokenize,ssplit,pos,lemma,ner,depparse,natlog, coref, openie','openie.resolve_coref': coref_flag}, timeout=30000, memory='16G') as client:
        counter = 1
        for query in input_json:
            query_json = dict()
            sentence_json = dict()
            query_json['queryid'] = query['queryid']
            query_json['contextid'] = query['contextid']
            query_json['contexttext'] = query['contexttext']
            query_json['contextrank'] = query['contextrank']
            query_json['contextscore'] = query['contextscore']
            try:
                annotated_text = client.annotate(query['contexttext'])

                relation_triples_list = []
                for s in annotated_text.sentence:
                    token_json = dict()
                    for tk in s.token:
                        tk_details = dict()
                        tk_details['token'] = tk.word
                        tk_details['charOffsetBegin'] = str(tk.beginChar)
                        tk_details['charOffsetEnd'] = str(tk.endChar)
                        token_json[tk.tokenBeginIndex] = tk_details
                    sentence_json[str(s.sentenceIndex)] = token_json
                    for triple in s.openieTriple:
                        triple_json = dict()
                        triple_json['subject'] = triple.subject
                        triple_json['relation'] = triple.relation
                        triple_json['object'] = triple.object
                        triple_json['subjectTokens'] = build_triples_charoffset_list(triple.subjectTokens, sentence_json, annotated_text)
                        triple_json['relationTokens'] = build_triples_charoffset_list(triple.relationTokens, sentence_json, annotated_text)
                        triple_json['objectTokens'] = build_triples_charoffset_list(triple.objectTokens, sentence_json, annotated_text)
                        relation_triples_list.append(triple_json)
                    query_json['relationTriples'] = relation_triples_list
            except AnnotationException as ae:
                error_log.append('%s %s %s %s' % query['queryid'], query['contextid'], 'AnnotationException', str(ae))
                query_json['relationTriples'] = []
            output_json.append(query_json)
            print(counter)
            counter = counter + 1
    return output_json, error_log


def write_file(output_file, output_dict):
    check_file_existence(output_file)
    with open(output_file, 'w', encoding='utf-8') as f1:
        json.dump(output_dict, f1)


if __name__ == "__main__":
    parser = argparse.ArgumentParser(
        "Please provide input json file and output json file location")
    parser.add_argument('--i', help='input json file location')
    parser.add_argument('--o', help='output json file location')
    parser.add_argument('--corefflag', help='coref flag')
    parser.add_argument('--e', help='error log file')
    args = parser.parse_args()
    if len(sys.argv) == 1:
        parser.print_help(sys.stderr)
        sys.exit(1)
    extracted_relations, error_logs = extract_relations(args.i, args.corefflag)
    readwriteutils.write_json_file(args.o, extracted_relations)
    readwriteutils.write_text_file(args.e, error_logs)
