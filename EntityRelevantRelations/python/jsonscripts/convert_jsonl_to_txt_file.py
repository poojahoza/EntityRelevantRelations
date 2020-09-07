import jsonlines
import argparse


def generate_trec_eval_run_file_from_jsonl(input_file_loc, output_file_loc, field):
    text_file_list = []
    with jsonlines.open(input_file_loc, "r") as f:
        for line in f:
            text_file_list.append(line["query"]+" Q0 "+line["document"][field]+" "+str(line["rank"])+" "+str(line["score"])+" unh_trema "+line["method"])

    with open(output_file_loc, "w", encoding='utf-8') as writer:
        for l in text_file_list:
            writer.write('%s\n' % l)

if __name__ == "__main__":
    parser = argparse.ArgumentParser("Please provide input jsonl file and output txt file location")
    parser.add_argument('--r', help='input-runs folder location')
    parser.add_argument('--o', help='output text file location')
    parser.add_argument('--f', help='values: paragraph|entity|neighbor')
    args = parser.parse_args()
    run_data = generate_trec_eval_run_file_from_jsonl(args.r, args.o, args.f)