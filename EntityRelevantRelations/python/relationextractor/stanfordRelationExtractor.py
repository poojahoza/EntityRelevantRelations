import json
import argparse
import os
import sys

from stanford.server import CoreNLPClient

def check_file_existence(output_file_name):
    if os.path.exists(output_file_name):
        os.remove(output_file_name)

def extract_relations(input, coref_flag):
    input_json = dict()
    output_json = []
    with open(input,'w') as f:
        input_json=json.load(f)
    with CoreNLPClient(properties={'annotators':'tokenize,ssplit,pos,lemma,ner,depparse,natlog, coref, openie','openie.resolve_coref':coref_flag},timeout=15000,memory='16G') as client:
        counter = 1
        for query in input_json:
            query_json = dict()
            query_json['queryid'] = query['queryid']
            query_json['contextid'] = query['contextid']
            query_json['contexttext'] = query['contexttext']
            query_json['contextrank'] = query['contextrank']
            query_json['contextscore'] = query['contextscore']
            annotated_text = client.annotate(query['contexttext'])
            for s in annotated_text['sentence']:
                for triple in annotated_text['sentence'][s]['openieTriple']:
                    triple_json = dict()
                    triple_json['subject'] = annotated_text['sentence'][s]['openieTriple'][triple]['subject']
                    triple_json['relation'] = annotated_text['sentence'][s]['openieTriple'][triple]['relation']
                    triple_json['object'] = annotated_text['sentence'][s]['openieTriple'][triple]['object']
                    relation_tokens=[]
                    for subtoken in annotated_text['sentence'][s]['openieTriple'][triple]['subjectTokens']:
                        for s1 in annotated_text['sentence']:
                            if annotated_text['sentence'][s1]['sentenceIndex'] == annotated_text['sentence'][s]['openieTriple'][triple]['subjectTokens'][subtoken]['sentenceIndex']:
                                for t in annotated_text['sentence'][s1]['token']:
                                    if annotated_text['sentence'][s1]['token'][t]['tokenBeginIndex'] == annotated_text['sentence'][s][subtoken]['tokenIndex']:
                                        token_details = dict()
                                        token_details['token'] = annotated_text['sentence'][s1]['token'][t]['word']
                                        token_details['charOffsetBegin'] = annotated_text['sentence'][s1]['token'][t]['beginChar']
                                        token_details['charOffsetEnd'] = annotated_text['sentence'][s1]['token'][t]['endChar']
                                        relation_tokens.append(token_details)
                                        break
                                break
                    triple_json['subjectTokens'] = relation_tokens

                    relation_tokens = []
                    for reltoken in annotated_text['sentence'][s]['openieTriple'][triple]['relationTokens']:
                        for s1 in annotated_text['sentence']:
                            if annotated_text['sentence'][s1]['sentenceIndex'] == annotated_text['sentence'][s]['openieTriple'][triple]['relationTokens'][reltoken]['sentenceIndex']:
                                for t in annotated_text['sentence'][s1]['token']:
                                    if annotated_text['sentence'][s1]['token'][t]['tokenBeginIndex'] == annotated_text['sentence'][s][reltoken]['tokenIndex']:
                                        token_details = dict()
                                        token_details['token'] = annotated_text['sentence'][s1]['token'][t]['word']
                                        token_details['charOffsetBegin'] = annotated_text['sentence'][s1]['token'][t]['beginChar']
                                        token_details['charOffsetEnd'] = annotated_text['sentence'][s1]['token'][t]['endChar']
                                        relation_tokens.append(token_details)
                                        break
                                break
                    triple_json['relationTokens'] = relation_tokens

                    relation_tokens = []
                    for objtoken in annotated_text['sentence'][s]['openieTriple'][triple]['objectTokens']:
                        for s1 in annotated_text['sentence']:
                            if annotated_text['sentence'][s1]['sentenceIndex'] == annotated_text['sentence'][s]['openieTriple'][triple]['objectTokens'][objtoken]['sentenceIndex']:
                                for t in annotated_text['sentence'][s1]['token']:
                                    if annotated_text['sentence'][s1]['token'][t]['tokenBeginIndex'] == annotated_text['sentence'][s][objtoken]['tokenIndex']:
                                        token_details = dict()
                                        token_details['token'] = annotated_text['sentence'][s1]['token'][t]['word']
                                        token_details['charOffsetBegin'] = annotated_text['sentence'][s1]['token'][t]['beginChar']
                                        token_details['charOffsetEnd'] = annotated_text['sentence'][s1]['token'][t]['endChar']
                                        relation_tokens.append(token_details)
                                        break
                                break
                    triple_json['objectTokens'] = relation_tokens
                query_json['relationTriples'] = triple_json
            output_json.append(query_json)
            print(counter)
            counter = counter + 1
            if counter == 6:
                break
    return output_json

def write_file(output_file, output_dict):
    check_file_existence(output_file)
    with open(output_file,'w',character_encoding='utf-8') as f1:
        json.dump(output_dict,f1)

if __name__ == "__main__":
    parser = argparse.ArgumentParser(
            "Please provide input json file and output json file location")
    parser.add_argument('--i', help='input json file location')
    parser.add_argument('--o',help='output json file location')
    parser.add_argument('--corefflag',help='coref flag')
    args = parser.parse_args()
    if len(sys.argv) == 1:
            parser.print_help(sys.stderr)
            sys.exit(1)
    extracted_relations = extract_relations(args.i, args.corefflag)
    write_file(args.o, extracted_relations)
