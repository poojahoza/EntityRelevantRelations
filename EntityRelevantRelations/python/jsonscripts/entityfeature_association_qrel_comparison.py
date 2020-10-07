import jsonlines
import argparse
import pandas as pd


def write_csv_file(output_file, output_dict):
    l = []
    for item in output_dict:
        l.append(pd.DataFrame(item, index=[0]))
    tmp = pd.concat(l)
    tmp.index.name = 'Queries'
    #tmp = tmp.rename(columns={0:'Total',1:'Common',2:'Difference'})
    tmp.to_csv(output_file)


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


def find_common_entities(feature_data, association_data, qrel_data):
    common_entities_list = []
    for query, ent in association_data.items():
        if query in feature_data:
            common_entities_dict = dict()
            common_entities_dict['queryid'] = query
            common_entities_dict['total_association_entities'] = len(ent)
            common_entities_dict['total_feature_entities'] = len(feature_data[query])
            common_entities_dict['total_qrel_entities'] = len(qrel_data[query])
            common_entities_dict['common_entities'] = len(ent & feature_data[query] & qrel_data[query])
            common_entities_list.append(common_entities_dict)
    return common_entities_list


def generate_associations_dict(associations_file):

    output_dict = dict()
    counter = 1

    with jsonlines.open(associations_file, 'r') as f:
        for item in f:
            entities_set = set()
            if item['query'] in output_dict:
                entities_set = output_dict[item['query']]
            entities_set.add(item['document']['entity'])
            output_dict[item['query']] = entities_set
            print(counter)
            counter = counter + 1
    return output_dict

if __name__ == "__main__":
    parser = argparse.ArgumentParser("Please provide associations file, feature file, qrel file and output file location")
    parser.add_argument('--a', help='ENT rank lips associations file location')
    parser.add_argument('--f', help='feature file location')
    parser.add_argument('--q', help='qrel file location')
    parser.add_argument('--o', help='output csv associations file location')
    args = parser.parse_args()
    association_data = generate_associations_dict(args.a)
    feature_data = generate_associations_dict(args.f)
    qrel_data = process_qrel_files(args.q)
    common_entities_list = find_common_entities(feature_data, association_data, qrel_data)
    write_csv_file(args.o, common_entities_list)