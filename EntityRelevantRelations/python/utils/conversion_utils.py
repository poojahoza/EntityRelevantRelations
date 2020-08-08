def convert_entity_counter_dict_to_trec_format(input_dict, method_name):
    final_output = []
    for query, val in input_dict.items():
        rank = 1
        for entity, score in val.items():
            final_output.append(query+" Q0 "+entity+" "+str(rank)+" "+str(score)+" unh_trema "+method_name)
            rank = rank + 1
    return final_output
