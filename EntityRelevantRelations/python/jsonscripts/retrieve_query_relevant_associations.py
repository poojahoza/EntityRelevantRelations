import jsonlines
import argparse


def write_jsonl_file(output_file, output_list):
    with jsonlines.open(output_file, mode='w') as f:
        for a in output_list:
            f.write(a)

def merge_associations(associations_file, query):

    output_list = []
    counter = 1

    with jsonlines.open(associations_file, 'r') as f:
        for item in f:
            if item['queryid'] == query:
                output_list.append(item)
                print(counter)
                counter = counter + 1
    return output_list

if __name__ == "__main__":
    parser = argparse.ArgumentParser("Please provide associations file and output file location")
    parser.add_argument('--a', help='ENT rank lips associations file location')
    parser.add_argument('--o', help='output jsonl associations file location')
    parser.add_argument('--query', help='query for which the associations to be retrieved')
    args = parser.parse_args()
    output_data = merge_associations(args.a, args.query)
    write_jsonl_file(args.o, output_data)