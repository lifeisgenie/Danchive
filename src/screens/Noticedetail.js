import React from 'react';
import { View, Text, StyleSheet, Button } from 'react-native';

const NoticeDetail = ({ route, navigation }) => {
  const { noticeId } = route.params;

  // 전달받은 noticeId를 기반으로 상세 내용 fetch하거나 처리할 수 있음

  return (
    <View style={styles.container}>
      <Text style={styles.title}>공지사항 상세</Text>
      <Text style={styles.noticeId}>공지 ID: {noticeId}</Text>

      {/* 예: 돌아가기 버튼 */}
      <Button title="뒤로가기" onPress={() => navigation.goBack()} />
    </View>
  );
};

const styles = StyleSheet.create({
  container: { flex:1, alignItems:'center', justifyContent:'center', backgroundColor:'#fff' },
  title: { fontSize:24, fontWeight:'bold', marginBottom:20 },
  noticeId: { fontSize:18 },
});

export default NoticeDetail;
