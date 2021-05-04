import jieba
import jieba.posseg as pseg


class Word(object):
    def __init__(self, token, pos):
        self.token = token
        self.pos = pos


class QueryNER:
    def __init__(self, path_list):
        """
        初始化, 加载外部词典
        :param dict_paths:
        """
        for p in path_list:
            jieba.load_userdict(p)

        # 这些词不要切分
        jieba.suggest_freq('其他名字', True)
        jieba.suggest_freq('其他名称', True)
        jieba.suggest_freq('上映地区', True)
        jieba.suggest_freq('上映国家', True)
        jieba.suggest_freq('上映时间', True)
        jieba.suggest_freq('上映语言', True)
        jieba.suggest_freq('评分人数', True)
        jieba.suggest_freq('详细介绍', True)
        jieba.suggest_freq('时长', True)
        jieba.suggest_freq('出生时间', True)




    def get_ner_objects(self, question):
        """
        对问题进行实体识别
        :param question:·
        :return:
        """
        return [Word(word, tag) for word, tag in pseg.cut(question)]


# if __name__ == '__main__':
#     question_ner = QueryNER(['book_and_movie_name.txt', 'person_name.txt'])
#     question = '吴京主演的战狼2评分人数有多少'
#     ner_object = question_ner.get_ner_objects(question)
#     for value in ner_object:
#         print(value.token, value.pos)