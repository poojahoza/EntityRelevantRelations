import os
import argparse
import json

def process_qrel_files(input_qrel_file):
    qrel_list = dict()
    with open(input_qrel_file, 'r', encoding='utf-8') as f:
        for line in f:
            data = line.split(" ")
            query_id = data[0]
            ent = data[2]
            val = set()
            if query_id in qrel_list:
                val = qrel_list[query_id]
            val.add(ent)
            qrel_list[query_id] = val
    #print(qrel_list)
    return qrel_list


def process_json_files(input_json_dir, qrel_dict, query, output_file_loc):
    files = os.listdir(input_json_dir)
    relevant_entities = set()
    final_output = []

    for file in files:
        with open(input_json_dir+'/'+file, 'r', encoding='utf-8') as f:
            json_decode = json.load(f)
            for item in json_decode:
                query_id = item.get("queryid")
                if query_id == query:
                    for relation in item['relAnnotations']:
                        sub_ann = []
                        obj_ann = []

                        for s_ann in relation['subjectAnnotations']:
                            sub_ann.extend(s_ann['wiki_converted_id'])
                        for o_ann in relation['objectAnnotations']:
                            obj_ann.extend(o_ann['wiki_converted_id'])

                        for s in sub_ann:
                            if s in qrel_dict[query]:
                                relevant_entities.add(s)
                        for o in obj_ann:
                            if o in qrel_dict[query]:
                                relevant_entities.add(o)

    print(relevant_entities)
    for ent in relevant_entities:
        final_output.append(query+" 0 "+ent+" 1")
    with open(output_file_loc, 'w', encoding='utf-8') as f:
        for line in final_output:
            f.write('%s\n' % line)
    return final_output
    #item_list.append(query_list)
    #with open(output, 'w', encoding='utf-8') as f:
    #        json.dump(item_list, f, default=set_default)



if __name__ == "__main__":
    parser = argparse.ArgumentParser("Please provide input JSON directory, Qrel file and query")
    parser.add_argument("--i", help="Input JSON folder location")
    parser.add_argument("--q", help="Input qrel file location")
    parser.add_argument("--o", help="output qrel text file location")
    parser.add_argument("--query", help="query to retrieve the relevant relational entities for")
    args = parser.parse_args()
    qrel_dict = process_qrel_files(args.q)
    json_dict = process_json_files(args.i, qrel_dict, args.query, args.o)
