import jsonlines
import argparse
import os


def write_jsonl_file(output_file, output_list):
    with jsonlines.open(output_file, mode='w') as f:
        for a in output_list:
            f.write(a)

def merge_associations(associations_folder):

    output_list = []
    counter = 1

    files = os.listdir(associations_folder)
    for file in files:
        with jsonlines.open(associations_folder+'/'+file, 'r') as f:
            for item in f:
                output_list.append(item)
                print(counter)
                counter = counter + 1
    return output_list

if __name__ == "__main__":
    parser = argparse.ArgumentParser("Please provide associations file folder and output file location")
    parser.add_argument('--a', help='ENT rank lips associations folder location')
    parser.add_argument('--o', help='output jsonl associations file location')
    args = parser.parse_args()
    output_data = merge_associations(args.a)
    write_jsonl_file(args.o, output_data)