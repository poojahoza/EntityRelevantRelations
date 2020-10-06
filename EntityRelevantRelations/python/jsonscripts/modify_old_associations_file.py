import jsonlines
import argparse
import os


def write_jsonl_file(output_file, output_list):
    with jsonlines.open(output_file, mode='w') as f:
        for a in output_list:
            f.write(a)

def change_old_associations(associations_file, para_flag):

    output_list = []
    counter = 1

    with jsonlines.open(associations_file, 'r') as f:
        for item in f:
            ann_dict = dict()
            ann_doc = dict()
            ann_dict['query'] = item['query']
            ann_dict['rank'] = item['rank']
            ann_dict['score'] = item['score']
            ann_dict['method'] = item['method']
            ann_doc['neighbor'] = []
            ann_doc['entity'] = item['document']['entity']
            if para_flag:
                ann_doc['paragraph'] = item['document']['paragraph']
            ann_dict['document'] = ann_doc
            output_list.append(ann_dict)
            print(counter)
            counter = counter + 1
    return output_list

if __name__ == "__main__":
    parser = argparse.ArgumentParser("Please provide associations file, paragraph flag and output file location")
    parser.add_argument('--a', help='ENT rank lips associations file location')
    parser.add_argument('--p', action='store_true', help='ENT rank lips associations file location')
    parser.add_argument('--o', help='output jsonl associations file location')
    args = parser.parse_args()
    output_data = change_old_associations(args.a, args.p)
    write_jsonl_file(args.o, output_data)