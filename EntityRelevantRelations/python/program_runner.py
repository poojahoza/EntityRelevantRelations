import argparse
import sys

if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    sub_parsers = parser.add_subparsers(help='sub-command help')

    parser_entitylinker = sub_parsers.add_parser('entitylinker', help='entity linker help')
    parser_entitylinker.add_argument("-wat", "--watlinker", action='store_true', help="use wat entity linker")
    parser_entitylinker.add_argument("-i", "--input", help="use wat entity linker")
    parser_entitylinker.add_argument("-o", "--output", help="use wat entity linker")

    parser_relation_extractor = sub_parsers.add_parser('relationextractor', help='relation extractor help')
    parser_relation_extractor.add_argument("-stanford", "--stanford", action='store_true', help="use Stanford " \
                                                                                                "relation extractor")
    parser_relation_extractor.add_argument('-i', '--input', help='input json file location')
    parser_relation_extractor.add_argument('-o', '--output', help='output json file location')
    parser_relation_extractor.add_argument('-coref', '--corefflag', help='coref flag')
    parser_relation_extractor.add_argument('-e', '--errorlog', help='error log file')

    parser_features = sub_parsers.add_parser('features', help='features help')
    parser_features.add_argument("-freq", "--entityfreq", action='store_true', help='execute entity freq feature')
    parser_features.add_argument('-a', '--annotations', help='relation triple file location')
    parser_features.add_argument('-f', '--field', help='field subject | object')
    parser_features.add_argument('-l', '--limit', type=int, help='top k elements limit')
    parser_features.add_argument('-o', '--output', help='json output file location')

    parser_ranklib = sub_parsers.add_parser('ranklib', help='ranklib help')
    parser_ranklib.add_argument('-q', '--qrel', help='qrel file location')
    parser_ranklib.add_argument('-f', '--feature', help='run file list', action="append")
    parser_ranklib.add_argument(
        '-z', "--zscore", help='normalize the feature vectors with zscore', action="store_true")
    parser_ranklib.add_argument(
        '-o', '--output', help='output feature vector file path without zscore')
    parser_ranklib.add_argument('-zo', '--outputzscore', help='output feature vector file path zscore')
    parser_ranklib.add_argument('-ranklibo', '--rankliboutput', help='output ranklib file path zscore')

    args = parser.parse_args()

    if len(sys.argv) == 1:
        parser.print_help(sys.stderr)
        sys.exit(1)


    print(args)

