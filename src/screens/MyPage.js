import React from 'react';
import { View, Text, StyleSheet, Image, TouchableOpacity, ScrollView } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

const userInfo = {
  profile: require('./assets/profile.jpg'),  // 실제 프로필 이미지 경로로 교체!
  name: '최해준',
  team: '이글점프',
  project: '작품명 예시',
};

const menuItems = [
  { title: '설정' },
  { title: '비밀번호 재설정' },
  { title: '회원탈퇴' },
];

const MyPage = () => {
  return (
    <SafeAreaView style={styles.container}>
      <ScrollView contentContainerStyle={{ flexGrow: 1 }}>
        {/* 상단 로고 (좌측 상단) */}
        <View style={styles.logoWrapper}>
          <Image source={require('./assets/logo.png')} style={styles.logo} />
        </View>
        {/* 프로필 섹션 */}
        <View style={styles.profileRow}>
          <Image source={userInfo.profile} style={styles.profileImage} />
          <View style={styles.infoSection}>
            <View style={styles.infoRow}>
              <Text style={styles.label}>이름</Text>
              <Text style={styles.value}>{userInfo.name}</Text>
            </View>
            <View style={styles.infoRow}>
              <Text style={styles.label}>협력 팀</Text>
              <Text style={styles.value}>{userInfo.team}</Text>
            </View>
            <View style={styles.infoRow}>
              <Text style={styles.label}>작품</Text>
              <Text style={styles.value}>{userInfo.project}</Text>
            </View>
          </View>
        </View>
        {/* 메뉴 및 설정 */}
        <View style={styles.menuSection}>
          {menuItems.map((item, idx) => (
            <TouchableOpacity key={idx} style={styles.menuItem}>
              <Text style={styles.menuText}>{item.title}</Text>
            </TouchableOpacity>
          ))}
        </View>
      </ScrollView>
      {/* 하단 탭바 등은 네비게이터에서 관리 */}
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#fff' },
  logoWrapper: { marginTop: 20, marginLeft: 18, marginBottom: 18 },
  logo: { width: 50, height: 18, resizeMode: 'contain' },
  profileRow: {
    flexDirection: 'row',
    alignItems: 'flex-start',
    paddingHorizontal: 28,
    marginBottom: 8,
  },
  profileImage: { width: 90, height: 110, borderRadius: 12, marginRight: 24, backgroundColor: '#ddd' },
  infoSection: { flex: 1 },
  infoRow: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 18,
    borderBottomWidth: 1,
    borderColor: '#eee',
    paddingBottom: 2,
  },
  label: { width: 80, fontSize: 16, color: '#777' },
  value: { fontSize: 16, color: '#222', flex: 1 },
  menuSection: { marginTop: 28, paddingHorizontal: 28 },
  menuItem: {
    paddingVertical: 16,
    borderBottomWidth: 1,
    borderColor: '#eee',
  },
  menuText: { fontSize: 16, color: '#444' },
});

export default MyPage;
