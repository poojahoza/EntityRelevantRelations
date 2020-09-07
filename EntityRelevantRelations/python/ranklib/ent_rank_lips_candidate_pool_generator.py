import argparse
import os


def write_text_file(final_list, output_file_loc):
    with open(output_file_loc, 'w', encoding='utf-8') as f:
        for line in final_list:
            f.write('%s\n' % line)


def convert_dict_to_list(final_dict):
    final_list = []

    for key, value in final_dict.items():
        for ent, rel in value.items():
            final_list.append(key+" 0 "+ent+" "+rel)

    return final_list


def generate_candidate_pool(qrel_dict, run_dict):

    final_dict = dict()

    for key, val in qrel_dict.items():
        sub_dict_qrel = dict()
        for entity in val:
            sub_dict_qrel[entity] = '1'
        final_dict[key] = sub_dict_qrel

    for key1, val1 in run_dict.items():
        if key1 in final_dict:
            run_sub_dict = final_dict[key1]
            for ent in val1:
                if ent not in run_sub_dict:
                    run_sub_dict[ent] = '0'
            final_dict[key1] = run_sub_dict

    output_dict = convert_dict_to_list(final_dict)
    return output_dict


def process_run_files(run_folder_loc):

    run_dict = dict()

    files = os.listdir(run_folder_loc)

    for file in files:
        with open(run_folder_loc+'/'+file, 'r') as r:
            for line in r.readlines():
                splitted_text = line.split()
                if splitted_text[0] in run_dict:
                    run_entities = run_dict[splitted_text[0]]
                    run_entities.add(splitted_text[2])
                    run_dict[splitted_text[0]] = run_entities
                else:
                    run_dict[splitted_text[0]] = set(splitted_text[2])
    return run_dict


def process_qrel_file(qrel_folder_loc):

    qrel_dict = dict()

    files = os.listdir(qrel_folder_loc)

    for file in files:
        with open(qrel_folder_loc+'/'+file, 'r') as f:
            for line in f.readlines():
                splitted_text = line.split()
                if splitted_text[0] in qrel_dict:
                    relevant_entities = qrel_dict[splitted_text[0]]
                    relevant_entities.add(splitted_text[2])
                    qrel_dict[splitted_text[0]] = relevant_entities
                else:
                    qrel_dict[splitted_text[0]] = set(splitted_text[2])
    return qrel_dict


if __name__ == "__main__":
    parser = argparse.ArgumentParser("Please provide qrel file and the run files location")
    parser.add_argument('--q', help='qrel text folder location')
    parser.add_argument('--r', help='input-runs folder location')
    parser.add_argument('--o', help='output text file location')
    args = parser.parse_args()
    qrel_data = process_qrel_file(args.q)
    run_data = process_run_files(args.r)
    output_data = generate_candidate_pool(qrel_data, run_data)
    write_text_file(output_data, args.o)

