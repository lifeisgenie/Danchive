import React, { useRef, useState } from 'react';
import { View, Text, StyleSheet, Image, TouchableOpacity, FlatList, Dimensions  } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { useNavigation } from '@react-navigation/native';

const screenWidth = Dimensions.get('window').width;
const cardWidth = screenWidth * 0.95; 
const cardHeight = cardWidth * 1.2;

const images = [
  require('./assets/sample-photo1.png'),
  require('./assets/sample-photo2.png'),
  require('./assets/sample-photo3.png'),
];

const cardInfos = [
  {
    title: "Communication Design\nThe 35TH Graduation",
    date: "2024.10.31 ~ 11.04",
  },
];

const notices = [
  { id: '1', title: '공지사항 1' },
  { id: '2', title: '공지사항 2' },
  { id: '3', title: '공지사항 3' },
];


const MainPage = () => {
  const [current, setCurrent] = useState(0);
  const flatRef = useRef(null);

  const navigation = useNavigation();

  const renderCard = ({ item, index }) => (
    <View style={styles.cardContainer}>
      <Image 
        source={item} 
        style={styles.cardImage} 
        resizeMode="stretch"
      />
      <View style={styles.cardOverlay}>
        <Text style={styles.cardOverlayTitle}>{cardInfos[index]?.title}</Text>
        <Text style={styles.cardOverlaySubtitle}>{cardInfos[index]?.date}</Text>
        <TouchableOpacity style={styles.cardCopyBtn}>
          <Text>📋</Text>
        </TouchableOpacity>
      </View>
      {index > 0 && (
        <TouchableOpacity style={styles.arrowLeft} onPress={() => flatRef.current.scrollToIndex({ index: index - 1 })}>
          <Text style={{ fontSize: 24 }}>◀</Text>
        </TouchableOpacity>
      )}
      {index < images.length - 1 && (
        <TouchableOpacity style={styles.arrowRight} onPress={() => flatRef.current.scrollToIndex({ index: index + 1 })}>
          <Text style={{ fontSize: 24 }}>▶</Text>
        </TouchableOpacity>
      )}
    </View>
  );

  // 공지사항 목록 화면 전체를 FlatList로 대체, 헤더에 메인 콘텐츠
    return (
    <SafeAreaView style={styles.container} edges={['top', 'left', 'right']}>
      <FlatList
        data={[{ key: 'dummy' }]}      // 위아래 스크롤 위해 dummy 데이터 1개
        keyExtractor={item => item.key}
        renderItem={() => null}        // 실제 아이템 렌더 X
        ListHeaderComponent={
          <>
            <View style={styles.header}>
              <Image source={require('./assets/logo.png')} style={styles.logo} />
            </View>
            <View style={styles.titleArea}>
              <Text style={styles.title}>2025학년도{'\n'}소프트웨어학과 졸업작품전시회 :</Text>
            </View>
            <View style={styles.sliderArea}>
              <FlatList
                ref={flatRef}
                data={images}
                horizontal
                pagingEnabled
                showsHorizontalScrollIndicator={false}
                keyExtractor={(item, idx) => idx.toString()}
                renderItem={renderCard}
                onMomentumScrollEnd={e => {
                  const index = Math.round(e.nativeEvent.contentOffset.x / cardWidth);
                  setCurrent(index);
                }}
                snapToInterval={cardWidth}
                decelerationRate="fast"
              />
            </View>
            <View style={styles.indicatorArea}>
              {images.map((_, idx) => (
                <View
                  key={idx}
                  style={[
                    styles.indicatorDot,
                    { backgroundColor: idx === current ? 'rgba(33,33,33,0.88)' : 'rgba(33,33,33,0.22)' }
                  ]}
                />
              ))}
            </View>
            <View style={styles.newsArea}>
              <Text style={styles.newsTitle}>NEWS</Text>
              <FlatList
                data={notices}
                keyExtractor={item => item.id}
                renderItem={({ item }) => (
                  <TouchableOpacity
                    style={styles.noticeItem}
                    onPress={() => navigation.navigate('NoticeDetail', { noticeId: item.id })}
                  >
                    <Text>{item.title}</Text>
                  </TouchableOpacity>
                )}
                style={{ maxHeight: 160 }}
                showsVerticalScrollIndicator={true}
              />
            </View>
          </>
        }
        contentContainerStyle={{ paddingBottom: 40 }}
      />
    </SafeAreaView>
  );

};

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#fff' },
  header: { flexDirection: 'row', alignItems: 'center', marginTop: 12, marginBottom: 8 },
  logo: { width: 46, height: 25, marginLeft: 16 },
  titleArea: { marginLeft: 16, marginBottom: 8 },
  title: { fontSize: 20, fontWeight: 800, lineHeight: 30 },
  sliderArea: { alignItems: 'center', marginBottom: 16 },
  cardContainer: {
    width: cardWidth,
    height: cardHeight,
    borderRadius: 25,
    backgroundColor: 'lightgray',
    overflow: 'hidden',
    marginHorizontal: 8,
    alignItems: "center",
    justifyContent: "center",
    shadowColor: '#000',
    shadowOffset: { width: 2, height: 4 },
    shadowOpacity: 0.15,
    shadowRadius: 10,
    elevation: 4,
    marginBottom: 8,
  },
  cardImage: {
    width: '100%',
    height: '100%',
    borderRadius: 25,
  },
  cardOverlay: {
    position: 'absolute',
    left: 18,
    bottom: 18,
    right: 18,
    backgroundColor: 'rgba(255,255,255,0.83)',
    borderRadius: 16,
    padding: 12,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.07,
    shadowRadius: 6,
    elevation: 2,
  },
  cardOverlayTitle: { fontSize: 17, fontWeight: "bold" },
  cardOverlaySubtitle: { fontSize: 13, fontWeight: "400", marginTop: 3 },
  cardCopyBtn: { position: "absolute", right: 10, bottom: 10, },
  arrowLeft: { position: "absolute", left: 4, top: "50%", marginTop: -14, zIndex: 1 },
  arrowRight: { position: "absolute", right: 4, top: "50%", marginTop: -14, zIndex: 1 },
  indicatorArea: { flexDirection: "row", justifyContent: "center", alignItems: "center", marginTop: 10, },
  indicatorDot: { width: 8, height: 8, borderRadius: 4, backgroundColor: "#333", marginHorizontal: 3 },
  newsArea: {
    marginTop: 10,
    width: cardWidth,
    height: cardHeight * 0.4,
    paddingHorizontal: 20,
    paddingVertical: 20,
    backgroundColor: "white",
    alignSelf: "center",
    borderRadius: 25,
    alignself: "center",
    elevation: 4,
    shadowColor: "#000",
    shadowOffset: { width: 2, height: 4 },
    shadowOpacity: 0.15,
    shadowRadius: 10
  },
  newsTitle: { fontSize: 17, fontWeight: "bold", marginBottom: 10 },
  noticeItem: { paddingVertical: 8, borderBottomWidth: 0.5, borderColor: "#eee" },
});

export default MainPage;
