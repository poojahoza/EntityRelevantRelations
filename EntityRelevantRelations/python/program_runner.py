import argparse
import sys

from entitylinker import WAT_entity_linker_wrapper
from features import annotations_entity_counter
from ranklib import ranklib_file_generator
from relationextractor import stanford_relation_extractor


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    sub_parsers = parser.add_subparsers(help='sub-command help')

    parser_entitylinker = sub_parsers.add_parser('entitylinker', help='entity linker help')
    parser_entitylinker.add_argument("-wat", "--watlinker", action='store_true', help="use wat entity linker")
    parser_entitylinker.add_argument("-i", "--input", help="use wat entity linker", required=True)
    parser_entitylinker.add_argument("-o", "--output", help="use wat entity linker", required=True)

    parser_relation_extractor = sub_parsers.add_parser('relationextractor', help='relation extractor help')
    parser_relation_extractor.add_argument("-stanford", "--stanford", action='store_true', help="use Stanford "
                                                                                                "relation extractor")
    parser_relation_extractor.add_argument('-i', '--input', help='input json file location', required=True)
    parser_relation_extractor.add_argument('-o', '--output', help='output json file location', required=True)
    parser_relation_extractor.add_argument('-coref', '--corefflag', help='coref flag true | false', required=True, choices=['true', 'false'])
    parser_relation_extractor.add_argument('-e', '--errorlog', help='error log file', required=True)

    parser_features = sub_parsers.add_parser('features', help='features help')
    parser_features.add_argument("-freq", "--entityfreq", action='store_true', help='execute entity freq feature')
    parser_features.add_argument('-a', '--annotations', help='relation triple folder location', required=True)
    parser_features.add_argument('-f', '--field', help='field subject | object', required=True, choices=['subject', 'object'])
    parser_features.add_argument('-l', '--limit', type=int, help='top k elements limit', required=True)
    parser_features.add_argument('-o', '--output', help='json output file location', required=True)

    parser_ranklib = sub_parsers.add_parser('ranklib', help='ranklib help')
    parser_ranklib.add_argument('-q', '--qrel', help='qrel file location', required=True)
    parser_ranklib.add_argument('-f', '--feature', help='run file list', action="append", required=True)
    parser_ranklib.add_argument(
        '-z', "--zscore", help='normalize the feature vectors with zscore', action="store_true", required=True)
    parser_ranklib.add_argument(
        '-o', '--output', help='output feature vector file path without zscore', required=True)
    parser_ranklib.add_argument('-zo', '--outputzscore', help='output feature vector file path zscore', required=True)
    parser_ranklib.add_argument('-ranklibo', '--rankliboutput', help='output ranklib file path zscore', required=True)

    args = parser.parse_args()

    if len(sys.argv) == 1:
        parser.print_help(sys.stderr)
        sys.exit(1)

    print(args)

    if args.watlinker:
        pass

    if args.stanford:
        pass

    if args.entityfreq:
        annotations_entity_counter.annotations_entity_counter_wrapper(args.annotations
                                                                      , args.field
                                                                      , args.limit
                                                                      , args.output)

    if args.ranklib:
        pass



