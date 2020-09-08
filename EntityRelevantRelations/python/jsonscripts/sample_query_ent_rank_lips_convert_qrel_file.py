import jsonlines
import argparse

def generate_sample_query_qrel_file_from_jsonl(input_qrel, output_qrel_txt, output_qrel_jsonl, query):
    output_file_list = []
    jsonl_output_list = []
    with jsonlines.open(input_qrel, "r") as f:
        for line in f:
            if line["query"] == query_id:
                #output_file_list.append(line)
                output_file_list.append(line["query"]+" 0 "+line["document"]["entity"]+" "+str(line["relevance"])
                output_dict= dict()
                output_dict["relevance"] = 1
                output_dict["query"] = line["query"]
                item_dict = dict()
                item_dict["entity"] = [line["document"]["entity"]]
                output_dict["document"] = item_dict
                jsonl_output_list.append(output_dict)

    with open(output_qrel_txt, "w", encoding='utf-8') as writer:
        for l in output_file_list:
            writer.write('%s\n' % l)

    with jsonlines.open(output_qrel_jsonl, mode='w') as f:
        for a in jsonl_output_list:
            f.write(a)


if __name__ == "__main__":
    parser = argparse.ArgumentParser("Please provide qrel jsonl file loc, output text file location, output jsonl file location and query")
    parser.add_argument('--q', help='entity qrel jsonl file location')
    parser.add_argument('--txt', help='output qrel text file location')
    parser.add_argument('--jsonl', help='output jsonl text file location')
    parser.add_argument('--query', help='sample query')
    args = parser.parse_args()
    generate_sample_query_qrel_file_from_jsonl(args.q, args.txt, args.jsonl, args.query)