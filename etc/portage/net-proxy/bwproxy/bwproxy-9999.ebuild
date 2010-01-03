EAPI=2

DESCRIPTION="A simple proxy server to limit the download speed of long TCP transmissions."
HOMEPAGE="http://gitorious.org/bwproxy/"

EGIT_REPO_URI="http://git.gitorious.org/bwproxy/bwproxy.git"
inherit git

LICENSE="GPL-3"
RESTRICT="mirror"
SLOT="0"
KEYWORDS="~amd64 ~x86"
IUSE=""

DEPEND="dev-java/ant sys-libs/libcmdargs sys-devel/gcc[gcj]"
RDEPEND="sys-libs/libcmdargs sys-devel/gcc[gcj]"

src_prepare() {
	ant distclean
}

src_compile() {
	ant gcj
}

src_install() {
	dobin "${S}/bwproxy"
	cp "${S}/etc/bwproxy.rc" "${T}/bwproxy"
	doinitd "${T}/bwproxy"
}
