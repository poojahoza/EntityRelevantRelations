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


def find_common_entities(feature_data, association_data):
    common_entities_list = []
    for query, ent in association_data.items():
        if query in feature_data:
            common_entities_dict = dict()
            common_entities_dict['queryid'] = query
            common_entities_dict['total_association_entities'] = len(ent)
            common_entities_dict['total_feature_entities'] = len(feature_data[query])
            common_entities_dict['common_entities'] = len(ent & feature_data[query])
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
    parser = argparse.ArgumentParser("Please provide associations file, feature file and output file location")
    parser.add_argument('--a', help='ENT rank lips associations file location')
    parser.add_argument('--f', help='feature file location')
    parser.add_argument('--o', help='output csv associations file location')
    args = parser.parse_args()
    association_data = generate_associations_dict(args.a)
    feature_data = generate_associations_dict(args.f)
    common_entities_list = find_common_entities(feature_data, association_data)
    write_csv_file(args.o, common_entities_list)