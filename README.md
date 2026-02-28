![メインブランチ CI](https://img.shields.io/badge/%E3%83%A1%E3%82%A4%E3%83%B3%E3%83%96%E3%83%A9%E3%83%B3%E3%83%81_CI-passing-brightgreen)
![CodeQL セキュリティ分析](https://img.shields.io/badge/CodeQL_%E3%82%BB%E3%82%AD%E3%83%A5%E3%83%AA%E3%83%86%E3%82%A3%E5%88%86%E6%9E%90-passing-brightgreen)
![OpenSSF Scorecard](https://img.shields.io/badge/openssf_scorecard-7.6-brightgreen)
![OpenSSF Best Practices](https://img.shields.io/badge/openssf_best_practices-silver-silver)
![ライセンス](https://img.shields.io/badge/%E3%83%A9%E3%82%A4%E3%82%BB%E3%83%B3%E3%82%B9-MIT-green)
![Java](https://img.shields.io/badge/Java-17-ED8B00)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2-6DB33F)

# Contoso モバイルバックエンド

Java Spring Boot と MySQL で構築された、Contoso モバイルアプリケーション向け REST API バックエンドです。

## 機能

- ユーザー登録と認証
- プッシュ通知管理
- コンテンツ配信 API
- アプリ内課金の検証
- ユーザープロフィール管理
- アクティビティログ

## クイックスタート

```bash
mvn spring-boot:run
```

## 技術スタック

- Java 17
- Spring Boot 3.2
- Spring Data JPA
- MySQL 8.0
- Redis（セッション管理）
- Firebase Cloud Messaging

## ライセンス

このプロジェクトは [MIT ライセンス](LICENSE)の下で公開されています。

## セキュリティ

脆弱性を発見された場合は、[セキュリティポリシー](SECURITY.md)をご確認ください。
