def validate_watlinker(parser_args):
    if parser_args['input'] is not None and parser_args['output'] is not None and parser_args['watlinker'] is not None:
        return True
    else:
        return False


def validate_stanford(parser_args):
    if parser_args['input'] is not None and parser_args['output'] is not None and parser_args['corefflag'] is not None and parser_args['errorlog'] is not None:
        return True
    else:
        return False


def validate_entityfreq(parser_args):
    if parser_args['annotations'] is not None and parser_args['field'] is not None and parser_args['limit'] is not None and parser_args['output'] is not None:
        return True
    else:
        return False


def validate_relations_freq(parser_args):
    if parser_args['annotations'] is not None and parser_args['limit'] is not None and parser_args['output'] is not None and parser_args['field'] is None:
        return True
    else:
        return False


def validate_relations_relevance(parser_args):
    if parser_args['annotations'] is not None and parser_args['limit'] is not None and parser_args['output'] is not None and parser_args['field'] is None:
        return True
    else:
        return False


def validate_ranklib(parser_args):
    if parser_args['qrel'] is not None and parser_args['feature'] is not None and parser_args['zscore'] is not None and parser_args['output'] is not None and parser_args['outputzscore'] is not None and parser_args['rankliboutput'] is not None:
        return True
    else:
        return False
