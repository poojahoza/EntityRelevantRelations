import argparse
import sys

import validate_runner_commands

from entitylinker import WAT_entity_linker_wrapper
from features import annotations_entity_counter, relations_freq, relations_relevance, relations_score, relations_proximity
from features import relations_degree
from ranklib import ranklib_file_generator
from relationextractor import stanford_relation_extractor


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    sub_parsers = parser.add_subparsers(help='sub-command help')

    parser_entitylinker = sub_parsers.add_parser('entitylinker', help='entity linker help')
    parser_entitylinker.add_argument("-wat", "--watlinker", action='store_true', help="use wat entity linker")
    parser_entitylinker.add_argument("-i", "--input", help="use wat entity linker")
    parser_entitylinker.add_argument("-o", "--output", help="use wat entity linker")

    parser_relation_extractor = sub_parsers.add_parser('relationextractor', help='relation extractor help')
    parser_relation_extractor.add_argument("-stanford", "--stanford", action='store_true', help="use Stanford "
                                                                                                "relation extractor")
    parser_relation_extractor.add_argument('-i', '--input', help='input json file location')
    parser_relation_extractor.add_argument('-o', '--output', help='output json file location')
    parser_relation_extractor.add_argument('-coref', '--corefflag', help='coref flag true | false', choices=['true', 'false'])
    parser_relation_extractor.add_argument('-e', '--errorlog', help='error log file')

    parser_features = sub_parsers.add_parser('features', help='features help')
    parser_features.add_argument("-freq", "--entityfreq", action='store_true', help='execute entity freq feature')
    parser_features.add_argument('-a', '--annotations', help='relation triple folder location')
    parser_features.add_argument('-f', '--field', help='field subject | object', choices=['subject', 'object', 'both'])
    parser_features.add_argument('-l', '--limit', type=int, help='top k elements limit')
    parser_features.add_argument('-o', '--output', help='text output file location')

    parser_features.add_argument("-relfreq", "--relationentityfreq", action='store_true', help='execute relation'
                                                                                               ' entity freq feature')

    parser_features.add_argument("-relrelevance", "--relationentityrelevance", action='store_true', help='execute '
                                                                                                         'relation '
                                                                                                         'entity relevance '
                                                                                                         'feature')

    parser_features.add_argument("-relscore", "--relationentityscore", action='store_true', help='execute relation '
                                                                                                 'entity score feature')

    parser_features.add_argument("-relprox", "--relationproximity", action='store_true', help='execute relation '
                                                                                                 'proximity feature')

    parser_features.add_argument("-reldeg", "--relationdegree", action='store_true', help='execute relation '
                                                                                                     'degree feature')

    parser_ranklib = sub_parsers.add_parser('ranklib', help='ranklib help')
    parser_ranklib.add_argument('-q', '--qrel', help='qrel file location')
    parser_ranklib.add_argument('-f', '--feature', help='run file list', action="append")
    parser_ranklib.add_argument('-z', "--zscore", help='normalize the feature vectors with zscore', action="store_true")
    parser_ranklib.add_argument('-o', '--output', help='output feature vector file path without zscore')
    parser_ranklib.add_argument('-zo', '--outputzscore', help='output feature vector file path zscore')
    parser_ranklib.add_argument('-ranklibo', '--rankliboutput', help='output ranklib file path zscore')

    args = parser.parse_args()

    if len(sys.argv) == 1:
        parser.print_help(sys.stderr)
        sys.exit(1)

    parser_arguments = vars(args)

    if 'watlinker' in parser_arguments and parser_arguments['watlinker']:
        if validate_runner_commands.validate_watlinker(parser_arguments):
            WAT_entity_linker_wrapper.wat_linker_wrapper(parser_arguments['input']
                                                         , parser_arguments['output'])

    if 'stanford' in parser_arguments:
        pass

    if 'entityfreq' in parser_arguments and parser_arguments['entityfreq']:
        if validate_runner_commands.validate_entityfreq(parser_arguments):
            annotations_entity_counter.annotations_entity_counter_wrapper(parser_arguments['annotations']
                                                                          , parser_arguments['field']
                                                                          , parser_arguments['limit']
                                                                          , parser_arguments['output'])
        else:
            parser.print_help(sys.stderr)
            sys.exit(1)

    if 'relationentityfreq' in parser_arguments and parser_arguments['relationentityfreq']:
        if validate_runner_commands.validate_relations_freq(parser_arguments):
            relations_freq.relation_freq_wrapper(parser_arguments['annotations']
                                                 , parser_arguments['limit']
                                                 , parser_arguments['output'])
        else:
            parser.print_help(sys.stderr)
            sys.exit(1)

    if 'relationentityrelevance' in parser_arguments and parser_arguments['relationentityrelevance']:
        if validate_runner_commands.validate_relations_relevance(parser_arguments):
            relations_relevance.relation_relevance_wrapper(parser_arguments['annotations']
                                                 , parser_arguments['limit']
                                                 , parser_arguments['output'])
        else:
            parser.print_help(sys.stderr)
            sys.exit(1)

    if 'relationentityscore' in parser_arguments and parser_arguments['relationentityscore']:
        if validate_runner_commands.validate_relations_relevance(parser_arguments):
            relations_score.relation_score_wrapper(parser_arguments['annotations']
                                                           , parser_arguments['limit']
                                                           , parser_arguments['output'])
        else:
            parser.print_help(sys.stderr)
            sys.exit(1)

    if 'relationproximity' in parser_arguments and parser_arguments['relationproximity']:
        if validate_runner_commands.validate_relations_proximity(parser_arguments):
            relations_proximity.relation_proximity_wrapper(parser_arguments['annotations']
                                                   , parser_arguments['output'])
        else:
            parser.print_help(sys.stderr)
            sys.exit(1)

    if 'relationdegree' in parser_arguments and parser_arguments['relationdegree']:
        if validate_runner_commands.validate_relations_proximity(parser_arguments):
            relations_degree.relation_degree_wrapper(parser_arguments['annotations']
                                                   , parser_arguments['output'])
        else:
            parser.print_help(sys.stderr)
            sys.exit(1)


    if 'ranklib' in parser_arguments:
        pass



