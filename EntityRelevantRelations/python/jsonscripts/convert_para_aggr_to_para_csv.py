import jsonlines
import csv
import argparse


def read_para_aggr_jsonl_file(input_file_location):
    output_list = []
    with jsonlines.open(input_file_location, 'r', encoding='utf-8') as reader:
        for item in reader:
            output_list.append(item['query']+'\t'+item['document']['paragraph'])
    return output_list


def write_para_aggr_to_csv(input_list, output_file_location):
    with open(output_file_location, 'w', encoding='utf-8') as f:
        wr = csv.writer(f, delimiter='\n')
        wr.writerow(input_list)


if __name__ == "__main__":
    parser = argparse.ArgumentParser("Please provide input para aggr jsonl file and output file path")
    parser.add_argument("--i", help="Input JSON folder location")
    parser.add_argument("--o", help="Output JSON file location")
    args = parser.parse_args()
    para_aggr_list = read_para_aggr_jsonl_file(args.i)
    write_para_aggr_to_csv(para_aggr_list, args.o)
