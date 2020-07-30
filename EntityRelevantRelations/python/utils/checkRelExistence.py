import argparse

from utils import readwriteutils


def count_relations(input_file):
    relation_triples = readwriteutils.read_json_file(input_file)
    log_file = []
    counter = 1
    for item in relation_triples:
        log_file.append(str(counter)+" "+item['queryid']+" "+item['contextid']+" "+str(len(item['relationTriples'])))
        print(counter)
        counter = counter + 1
    return log_file


if __name__ == "__main__":
    parser = argparse.ArgumentParser("Please provid input relation triple file and output file location")
    parser.add_argument('--r', help='relation triple file location')
    parser.add_argument('--o', help='json output file location')
    args = parser.parse_args()
    logs = count_relations(args.r)
    readwriteutils.write_text_file(args.o, logs)
