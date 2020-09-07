import argparse
import json
import jsonlines


def write_jsonl_file(output_file, output_list):
    with jsonlines.open(output_file, mode='w') as f:
        f.write(output_list)


def generate_assocations_file(input_folder,method_name):

    output_list = []

    files = os.listdir(input_folder)
    for file in files:
        with open(input_folder+'/'+file, 'r') as f:
            input_json = json.load(f)
            for item in input_json:
                for rel in item['relAnnotations']:
                    sub_ann = []
                    obj_ann = []
                    for sub in rel['subjectAnnotations']:
                        sub_ann.extend(sub['wiki_converted_id'])
                    for obj in rel['objectAnnotations']:
                        obj_ann.extend(obj['wiki_converted_id'])

                    for s in sub_ann:
                        for o in obj_ann:
                            ann_dict = dict()
                            ann_doc = dict()
                            ann_dict['query'] = item['queryid']
                            ann_dict['rank'] = "1"
                            ann_dict['score'] = "1"
                            ann_dict['method'] = method_name
                            ann_doc['paragraph'] = item['contextid']
                            ann_doc['neighbor'] = []
                            ann_doc['entity'] = [s, o]
                            ann_dict['document'] = ann_doc
                            output_list.append(ann_dict)
    return output_list


if __name__ == "__main__":
    parser = argparse.ArgumentParser("Please provide relation annotations folder, output file location and method name")
    parser.add_argument('--a', help='relation annotations folder location')
    parser.add_argument('--o', help='output jsonl associations file location')
    parser.add_argument('--m', help='method name')
    args = parser.parse_args()
    output_data = generate_assocations_file(args.a, args.m)
    write_jsonl_file(args.o, output_data)
