import argparse

from python.utils import readwriteutils


def map_relation_triples_wat_ann(rel_dict, ann_dict, ent_dict):

    query_rel_ann_list = []
    for query_con in rel_dict:
        query_con_dict = dict()
        rel_ann_list = []
        for triple in rel_dict[query_con]['relationTriples']:
            rel_ann_dict = dict()
            subject_tokens_list = []
            for token in rel_dict[query_con]['relationTriples'][triple]['subjectTokens']:
                for ann in ann_dict[query_con]['WATannotations']:
                    if rel_dict[query_con]['relationTriples'][triple]['subjectTokens'][token]['beginChar'] >= ann_dict[query_con]['WATannotations'][ann]['start'] and rel_dict[query_con]['relationTriples'][triple]['subjectTokens'][token]['endChar'] <= ann_dict[query_con]['WATannotations'][ann]['end']:
                        title_exists = False
                        for t in subject_tokens_list:
                            if subject_tokens_list[t]['wiki_title'] == ann_dict[query_con]['WATannotations'][ann]['wiki_title']:
                                title_exists = True
                        if title_exists == False:
                            subject_token_dict = dict()
                            subject_token_dict['wiki_id'] = ann_dict[query_con]['WATannotations'][ann]['wiki_id']
                            subject_token_dict['wiki_title'] = ann_dict[query_con]['WATannotations'][ann]['wiki_title']
                            subject_token_dict['wiki_converted_id'] = ent_dict[ann_dict[query_con]['WATannotations'][ann]['wiki_title']]
                            subject_tokens_list.append(subject_token_dict)
            object_tokens_list = []
            for token in rel_dict[query_con]['relationTriples'][triple]['objectTokens']:
                for ann in ann_dict[query_con]['WATannotations']:
                    if rel_dict[query_con]['relationTriples'][triple]['objectTokens'][token]['beginChar'] >= ann_dict[query_con]['WATannotations'][ann]['start'] and rel_dict[query_con]['relationTriples'][triple]['objectTokens'][token]['endChar'] <= ann_dict[query_con]['WATannotations'][ann]['end']:
                        title_exists = False
                        for t in object_tokens_list:
                            if object_tokens_list[t]['wiki_title'] == ann_dict[query_con]['WATannotations'][ann]['wiki_title']:
                                title_exists = True
                        if not title_exists:
                            object_token_dict = dict()
                            object_token_dict['wiki_id'] = ann_dict[query_con]['WATannotations'][ann]['wiki_id']
                            object_token_dict['wiki_title'] = ann_dict[query_con]['WATannotations'][ann]['wiki_title']
                            object_token_dict['wiki_converted_id'] = ent_dict[ann_dict[query_con]['WATannotations'][ann]['wiki_title']]
                            object_tokens_list.append(object_token_dict)
            rel_ann_dict['subject'] = rel_dict[query_con]['relationTriples'][triple]['subject']
            rel_ann_dict['relation'] = rel_dict[query_con]['relationTriples'][triple]['relation']
            rel_ann_dict['object'] = rel_dict[query_con]['relationTriples'][triple]['object']
            rel_ann_dict['subjectAnnotations'] = subject_tokens_list
            rel_ann_dict['objectAnnotations'] = object_tokens_list
            rel_ann_list.append(rel_ann_dict)
        query_con_id = query_con.split('_')
        query_con_dict['queryid'] = query_con_id[0]
        query_con_dict['contextid'] = query_con_id[1]
        query_con_dict['contexttext'] = rel_dict[query_con]['contexttext']
        query_con_dict['contextrank'] = rel_dict[query_con]['contextrank']
        query_con_dict['contextscore'] = rel_dict[query_con]['contextscore']
        query_con_dict['relAnnotations'] = rel_ann_list
        query_rel_ann_list.append(query_con_dict)

    return query_rel_ann_list


def create_relation_triples_wat_ann_dicts(rel_file, ann_file, ent_file):

    relation_triples = readwriteutils.read_json_file(rel_file)
    annotations = readwriteutils.read_json_file(ann_file)
    entity_ids = readwriteutils.read_converted_entity_ids(ent_file)

    rel_triples_dict = dict()
    ann_dict = dict()

    for item in relation_triples:
        rel_triples_details = dict()
        rel_triples_details['contexttext'] = relation_triples[item]['contexttext']
        rel_triples_details['contextrank'] = relation_triples[item]['contextrank']
        rel_triples_details['contextscore'] = relation_triples[item]['contextscore']
        rel_triples_details['relationTriples'] = relation_triples[item]['relationTriples']
        rel_triples_dict[relation_triples[item]['queryid']+'_'+relation_triples[item]['contextid']] = rel_triples_details

    for item in annotations:
        ann_details = dict()
        ann_details['contexttext'] = annotations[item]['contexttext']
        ann_details['contextrank'] = annotations[item]['contextrank']
        ann_details['contextscore'] = annotations[item]['contextscore']
        ann_details['WATannotations'] = annotations[item]['WATannotations']
        ann_dict[annotations[item]['queryid']+'_'+annotations[item]['contextid']] = ann_details

    return rel_triples_dict, ann_dict, entity_ids


def relation_triples_wat_annotations(rel_triples, annotations, ent_conv, output_file):
    rel_tri, wat_ann, ent_conv_id = create_relation_triples_wat_ann_dicts(rel_triples, annotations, ent_conv)
    output_json = map_relation_triples_wat_ann(rel_tri, wat_ann, ent_conv_id)
    readwriteutils.write_json_file(output_file, output_json)


if __name__ == "__main__":
    parser = argparse.ArgumentParser("Please provid input relation triple file, WAT Annotations file, "
                                     "entity conversion folder, output file locations")
    parser.add_argument('--r', help='relation triple file location')
    parser.add_argument('--w', help='WAT Annptations file location')
    parser.add_argument('--e', help='Entity conversions folder location')
    parser.add_argument('--o', help='json output file location')
    args = parser.parse_args()
    relation_triples_wat_annotations(args.r, args.w, args.e, args.o)
